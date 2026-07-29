// This file mirrors com.apps.quantitymeasurement.unit.* enums exactly.
// If UC21+ adds units on the backend, add them here too — nothing else
// needs to change since every form reads from these lists.

export const MEASUREMENT_TYPES = [
  { key: "LengthUnit", label: "Length" },
  { key: "WeightUnit", label: "Weight" },
  { key: "TemperatureUnit", label: "Temperature" },
  { key: "VolumeUnit", label: "Volume" },
];

// One canonical unit is shown per physical unit (backend has harmless
// aliases like METER/METERS and LITRE/LITER — we surface a single label
// each to keep the UI unambiguous).
export const UNITS_BY_TYPE = {
  LengthUnit: ["FEET", "INCHES", "YARDS", "CENTIMETERS", "METER"],
  WeightUnit: ["KILOGRAM", "GRAM", "POUND", "OUNCE", "TONNE"],
  TemperatureUnit: ["CELSIUS", "FAHRENHEIT", "KELVIN"],
  VolumeUnit: ["LITRE", "MILLILITRE", "GALLON"],
};

export const OPERATIONS = [
  { key: "COMPARE", label: "Compare" },
  { key: "CONVERT", label: "Convert" },
  { key: "ADD", label: "Add" },
  { key: "SUBTRACT", label: "Subtract" },
  { key: "DIVIDE", label: "Divide" },
];

// Which operations are allowed for a given measurement type (UC20 rule:
// Temperature only supports Compare + Convert).
export function operationsFor(measurementType) {
  if (measurementType === "TemperatureUnit") {
    return OPERATIONS.filter((op) => op.key === "COMPARE" || op.key === "CONVERT");
  }
  return OPERATIONS;
}

export function defaultUnit(measurementType) {
  return UNITS_BY_TYPE[measurementType][0];
}

export function formatNumber(value) {
  if (value === null || value === undefined || Number.isNaN(value)) return "—";
  const rounded = Math.round(value * 10000) / 10000;
  return rounded.toString();
}
