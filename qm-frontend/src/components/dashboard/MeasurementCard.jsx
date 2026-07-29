import { useEffect, useMemo, useState } from "react";
import { FiActivity, FiCopy, FiCheck } from "react-icons/fi";
import { quantityService } from "../../services/quantityService";
import { useToast } from "../../hooks/useToast";
import { useClipboard } from "../../hooks/useClipboard";
import { useHistory } from "../../hooks/useHistory";
import {
  MEASUREMENT_TYPES,
  UNITS_BY_TYPE,
  operationsFor,
  defaultUnit,
  formatNumber,
} from "../../utils/units";
import { validateMeasurementForm } from "../../utils/validation";
import "./MeasurementCard.css";

const emptyResult = null;

const OPERATION_LABELS = {
  COMPARE: "Compare",
  CONVERT: "Convert",
  ADD: "Add",
  SUBTRACT: "Subtract",
  DIVIDE: "Divide",
};

export default function MeasurementCard() {
  const toast = useToast();
  const { copied, copy } = useClipboard();
  const { refresh: refreshHistory } = useHistory();

  const [type, setType] = useState("LengthUnit");
  const [operation, setOperation] = useState("COMPARE");

  const [firstValue, setFirstValue] = useState("1");
  const [firstUnit, setFirstUnit] = useState(defaultUnit("LengthUnit"));
  const [secondValue, setSecondValue] = useState("1");
  const [secondUnit, setSecondUnit] = useState(defaultUnit("LengthUnit"));
  const [targetUnit, setTargetUnit] = useState(defaultUnit("LengthUnit"));
  const [resultUnit, setResultUnit] = useState(defaultUnit("LengthUnit"));

  const [result, setResult] = useState(emptyResult);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [touched, setTouched] = useState({});

  const units = UNITS_BY_TYPE[type];
  const allowedOps = operationsFor(type);
  const needsSecondValue = operation !== "CONVERT";
  const needsTargetUnit = operation === "CONVERT";

  // Whenever the measurement type changes, reset units/operation/result/
  // validation state so nothing stale carries over between types.
  useEffect(() => {
    const first = defaultUnit(type);
    setFirstUnit(first);
    setSecondUnit(units[1] || first);
    setTargetUnit(units[1] || first);
    setResultUnit(first);
    setResult(emptyResult);
    setFieldErrors({});
    setTouched({});
    if (!operationsFor(type).some((op) => op.key === operation)) {
      setOperation("COMPARE");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [type]);

  useEffect(() => {
    setResult(emptyResult);
    setFieldErrors({});
    setTouched({});
  }, [operation]);

  const liveValidation = useMemo(
    () =>
      validateMeasurementForm({
        measurementType: type,
        operation,
        firstUnit,
        firstValue,
        secondUnit,
        secondValue,
        targetUnit,
        needsSecondValue,
        needsTargetUnit,
      }),
    [type, operation, firstUnit, firstValue, secondUnit, secondValue, targetUnit, needsSecondValue, needsTargetUnit]
  );

  function markTouched(field) {
    setTouched((t) => ({ ...t, [field]: true }));
  }

  function errorFor(field) {
    return touched[field] ? fieldErrors[field] : undefined;
  }

  async function handleSubmit(e) {
    e.preventDefault();

    // Validate before making any API request; surface every error at once.
    const { fieldErrors: errors, isValid } = liveValidation;
    setFieldErrors(errors);
    setTouched({
      firstValue: true,
      firstUnit: true,
      secondValue: true,
      secondUnit: true,
      targetUnit: true,
    });
    if (!isValid) return;

    // Guard against duplicate submissions (double-click / double-Enter).
    if (submitting) return;
    setSubmitting(true);
    setLoading(true);

    try {
      let data;
      switch (operation) {
        case "COMPARE":
          data = await quantityService.compare(firstValue, firstUnit, secondValue, secondUnit, type);
          break;
        case "CONVERT":
          data = await quantityService.convert(firstValue, firstUnit, targetUnit, type);
          break;
        case "ADD":
          data = await quantityService.add(firstValue, firstUnit, secondValue, secondUnit, type);
          setResultUnit(data.resultUnit || firstUnit);
          break;
        case "SUBTRACT":
          data = await quantityService.subtract(firstValue, firstUnit, secondValue, secondUnit, type);
          setResultUnit(data.resultUnit || firstUnit);
          break;
        case "DIVIDE":
          data = await quantityService.divide(firstValue, firstUnit, secondValue, secondUnit, type);
          break;
        default:
          throw new Error("Unknown operation");
      }
      setResult(data);
      refreshHistory();
      toast.success(`${OPERATION_LABELS[operation]} completed successfully.`);
    } catch (err) {
      setResult(emptyResult);
      toast.error(extractError(err));
    } finally {
      setLoading(false);
      setSubmitting(false);
    }
  }

  // Changing the Result Unit dropdown re-calls the *-with-target-unit
  // endpoint live, without the user re-submitting the form.
  async function handleResultUnitChange(newUnit) {
    setResultUnit(newUnit);
    if (!result || (operation !== "ADD" && operation !== "SUBTRACT")) return;
    setLoading(true);
    try {
      const data =
        operation === "ADD"
          ? await quantityService.addWithTargetUnit(firstValue, firstUnit, secondValue, secondUnit, newUnit, type)
          : await quantityService.subtractWithTargetUnit(firstValue, firstUnit, secondValue, secondUnit, newUnit, type);
      setResult(data);
      refreshHistory();
    } catch (err) {
      toast.error(extractError(err));
    } finally {
      setLoading(false);
    }
  }

  function resultAsText() {
    if (!result) return "";
    if (operation === "COMPARE") return result.resultString === "true" ? "EQUAL" : "NOT EQUAL";
    if (operation === "CONVERT") return `${formatNumber(result.resultValue)} ${result.resultUnit}`;
    if (operation === "ADD" || operation === "SUBTRACT") return `${formatNumber(result.resultValue)} ${result.resultUnit}`;
    if (operation === "DIVIDE") return formatNumber(result.resultValue);
    return "";
  }

  async function handleCopy() {
    const text = resultAsText();
    if (!text) return;
    const ok = await copy(text);
    if (ok) {
      toast.success("Result copied successfully.");
    } else {
      toast.error("Could not copy result to clipboard.");
    }
  }

  return (
    <div className="measurement-card card">
      <div className="mc-header">
        <div className="mc-title">
          <FiActivity /> <h3>Measurement Module</h3>
        </div>
        <div className="mc-type-tabs" role="tablist" aria-label="Measurement type">
          {MEASUREMENT_TYPES.map((m) => (
            <button
              key={m.key}
              type="button"
              role="tab"
              aria-selected={type === m.key}
              className={"mc-tab" + (type === m.key ? " mc-tab-active" : "")}
              onClick={() => setType(m.key)}
            >
              {m.label}
            </button>
          ))}
        </div>
      </div>

      <div className="mc-op-tabs" role="tablist" aria-label="Operation">
        {allowedOps.map((op) => (
          <button
            key={op.key}
            type="button"
            role="tab"
            aria-selected={operation === op.key}
            className={"mc-op" + (operation === op.key ? " mc-op-active" : "")}
            onClick={() => setOperation(op.key)}
          >
            {op.label}
          </button>
        ))}
      </div>

      <form className="mc-form" onSubmit={handleSubmit} noValidate>
        <div className="mc-fields-grid">
        <div className="mc-row">
          <div className="field mc-field-value">
            <label htmlFor="mc-first-value">First Quantity</label>
            <input
              id="mc-first-value"
              type="text"
              inputMode="decimal"
              className={errorFor("firstValue") ? "field-invalid" : ""}
              value={firstValue}
              onChange={(e) => setFirstValue(e.target.value)}
              onBlur={() => markTouched("firstValue")}
              aria-invalid={Boolean(errorFor("firstValue"))}
              aria-describedby="mc-first-value-error"
            />
            {errorFor("firstValue") && (
              <span id="mc-first-value-error" className="field-error">
                {errorFor("firstValue")}
              </span>
            )}
          </div>
          <div className="field mc-field-unit">
            <label htmlFor="mc-first-unit">Unit</label>
            <select
              id="mc-first-unit"
              value={firstUnit}
              onChange={(e) => setFirstUnit(e.target.value)}
              onBlur={() => markTouched("firstUnit")}
            >
              {units.map((u) => (
                <option key={u} value={u}>
                  {u}
                </option>
              ))}
            </select>
          </div>
        </div>

        {needsSecondValue && (
          <div className="mc-row">
            <div className="field mc-field-value">
              <label htmlFor="mc-second-value">Second Quantity</label>
              <input
                id="mc-second-value"
                type="text"
                inputMode="decimal"
                className={errorFor("secondValue") ? "field-invalid" : ""}
                value={secondValue}
                onChange={(e) => setSecondValue(e.target.value)}
                onBlur={() => markTouched("secondValue")}
                aria-invalid={Boolean(errorFor("secondValue"))}
                aria-describedby="mc-second-value-error"
              />
              {errorFor("secondValue") && (
                <span id="mc-second-value-error" className="field-error">
                  {errorFor("secondValue")}
                </span>
              )}
            </div>
            <div className="field mc-field-unit">
              <label htmlFor="mc-second-unit">Unit</label>
              <select
                id="mc-second-unit"
                value={secondUnit}
                onChange={(e) => setSecondUnit(e.target.value)}
                onBlur={() => markTouched("secondUnit")}
              >
                {units.map((u) => (
                  <option key={u} value={u}>
                    {u}
                  </option>
                ))}
              </select>
            </div>
          </div>
        )}
        </div>

        {needsTargetUnit && (
          <div className="field">
            <label htmlFor="mc-target-unit">Target Unit</label>
            <select
              id="mc-target-unit"
              value={targetUnit}
              onChange={(e) => setTargetUnit(e.target.value)}
              onBlur={() => markTouched("targetUnit")}
            >
              {units.map((u) => (
                <option key={u} value={u}>
                  {u}
                </option>
              ))}
            </select>
          </div>
        )}

        <button
          className="btn btn-primary btn-block"
          type="submit"
          disabled={loading || submitting || !liveValidation.isValid}
        >
          {loading ? "Calculating…" : `Run ${operation.charAt(0) + operation.slice(1).toLowerCase()}`}
        </button>
      </form>

      {result && (
        <div className="mc-result">
          <div className="mc-result-summary">
            <span className="badge badge-success">
              <FiCheck size={12} /> Success
            </span>
            <span className="mc-result-op mono">
              {OPERATION_LABELS[operation]} · {formatNumber(Number(firstValue))} {firstUnit}
              {needsSecondValue && ` & ${formatNumber(Number(secondValue))} ${secondUnit}`}
              {needsTargetUnit && ` → ${targetUnit}`}
            </span>
          </div>
          <div className="mc-result-top">
            {operation === "COMPARE" && (
              <p className="mc-result-line">
                Result:{" "}
                <span className={"mono " + (result.resultString === "true" ? "accent-good" : "accent-bad")}>
                  {result.resultString === "true" ? "EQUAL" : "NOT EQUAL"}
                </span>
              </p>
            )}

            {operation === "CONVERT" && (
              <p className="mc-result-line">
                Result:{" "}
                <span className="mono accent-good">
                  {formatNumber(result.resultValue)} {result.resultUnit}
                </span>
              </p>
            )}

            {(operation === "ADD" || operation === "SUBTRACT") && (
              <p className="mc-result-line">
                Result:{" "}
                <span className="mono accent-good">
                  {formatNumber(result.resultValue)} {result.resultUnit}
                </span>
              </p>
            )}

            {operation === "DIVIDE" && (
              <p className="mc-result-line">
                Result (ratio): <span className="mono accent-good">{formatNumber(result.resultValue)}</span>
              </p>
            )}

            <button type="button" className="btn btn-secondary mc-copy-btn" onClick={handleCopy}>
              {copied ? (
                <>
                  <FiCheck /> Copied
                </>
              ) : (
                <>
                  <FiCopy /> Copy Result
                </>
              )}
            </button>
          </div>

          {(operation === "ADD" || operation === "SUBTRACT") && (
            <div className="field mc-result-unit-field">
              <label htmlFor="mc-result-unit">Result Unit</label>
              <select id="mc-result-unit" value={resultUnit} onChange={(e) => handleResultUnitChange(e.target.value)}>
                {units.map((u) => (
                  <option key={u} value={u}>
                    {u}
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

function extractError(err) {
  if (err.response?.data?.message) return err.response.data.message;
  if (err.response?.status === 401) return "Your session has expired. Please log in again.";
  return "Something went wrong while running that operation. Please check your inputs.";
}
