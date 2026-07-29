// Centralized validation so MeasurementCard's form logic reads as rules,
// not scattered inline conditionals.

export function isBlank(value) {
  return value === null || value === undefined || String(value).trim() === "";
}

// Numeric guard: rejects blank, non-numeric text, NaN, and +/-Infinity.
// Returns an error string, or null when the value is valid.
export function validateNumericField(value, label) {
  if (isBlank(value)) return `${label} is required.`;
  const num = Number(value);
  if (Number.isNaN(num)) return `${label} must be a valid number.`;
  if (!Number.isFinite(num)) return `${label} must be a finite number.`;
  return null;
}

export function validateRequired(value, label) {
  return isBlank(value) ? `${label} is required.` : null;
}

// Runs the full rule set for the measurement form and returns
// { fieldErrors: {key: message}, isValid: boolean }.
export function validateMeasurementForm({
  measurementType,
  operation,
  firstUnit,
  firstValue,
  secondUnit,
  secondValue,
  targetUnit,
  needsSecondValue,
  needsTargetUnit,
}) {
  const fieldErrors = {};

  if (isBlank(measurementType)) fieldErrors.measurementType = "Measurement type is required.";
  if (isBlank(operation)) fieldErrors.operation = "Operation is required.";
  if (isBlank(firstUnit)) fieldErrors.firstUnit = "Unit is required.";

  const firstErr = validateNumericField(firstValue, "First value");
  if (firstErr) fieldErrors.firstValue = firstErr;

  if (needsSecondValue) {
    if (isBlank(secondUnit)) fieldErrors.secondUnit = "Unit is required.";
    const secondErr = validateNumericField(secondValue, "Second value");
    if (secondErr) fieldErrors.secondValue = secondErr;
  }

  if (needsTargetUnit && isBlank(targetUnit)) {
    fieldErrors.targetUnit = "Target unit is required.";
  }

  return { fieldErrors, isValid: Object.keys(fieldErrors).length === 0 };
}
