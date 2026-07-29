import api from "./api";

const BASE = "/api/v1/quantities";

function q(value, unit, measurementType) {
  return { value: Number(value), unit, measurementType };
}

export const quantityService = {
  compare(a, unitA, b, unitB, measurementType) {
    return api
      .post(`${BASE}/compare`, {
        thisQuantityDTO: q(a, unitA, measurementType),
        thatQuantityDTO: q(b, unitB, measurementType),
      })
      .then((res) => res.data);
  },

  // `thatQuantityDTO.value` is ignored by the backend for convert — only
  // its unit matters (it's the target unit to convert into).
  convert(value, fromUnit, toUnit, measurementType) {
    return api
      .post(`${BASE}/convert`, {
        thisQuantityDTO: q(value, fromUnit, measurementType),
        thatQuantityDTO: q(1, toUnit, measurementType),
      })
      .then((res) => res.data);
  },

  add(a, unitA, b, unitB, measurementType) {
    return api
      .post(`${BASE}/add`, {
        thisQuantityDTO: q(a, unitA, measurementType),
        thatQuantityDTO: q(b, unitB, measurementType),
      })
      .then((res) => res.data);
  },

  addWithTargetUnit(a, unitA, b, unitB, targetUnit, measurementType) {
    return api
      .post(`${BASE}/add-with-target-unit`, {
        thisQuantityDTO: q(a, unitA, measurementType),
        thatQuantityDTO: q(b, unitB, measurementType),
        targetQuantityDTO: q(0, targetUnit, measurementType),
      })
      .then((res) => res.data);
  },

  subtract(a, unitA, b, unitB, measurementType) {
    return api
      .post(`${BASE}/subtract`, {
        thisQuantityDTO: q(a, unitA, measurementType),
        thatQuantityDTO: q(b, unitB, measurementType),
      })
      .then((res) => res.data);
  },

  subtractWithTargetUnit(a, unitA, b, unitB, targetUnit, measurementType) {
    return api
      .post(`${BASE}/subtract-with-target-unit`, {
        thisQuantityDTO: q(a, unitA, measurementType),
        thatQuantityDTO: q(b, unitB, measurementType),
        targetQuantityDTO: q(0, targetUnit, measurementType),
      })
      .then((res) => res.data);
  },

  divide(a, unitA, b, unitB, measurementType) {
    return api
      .post(`${BASE}/divide`, {
        thisQuantityDTO: q(a, unitA, measurementType),
        thatQuantityDTO: q(b, unitB, measurementType),
      })
      .then((res) => res.data);
  },

  getHistoryByOperation(operation) {
    return api.get(`${BASE}/history/operation/${operation}`).then((res) => res.data);
  },

  getHistoryByType(type) {
    return api.get(`${BASE}/history/type/${type}`).then((res) => res.data);
  },

  getOperationCount(operation) {
    return api.get(`${BASE}/count/${operation}`).then((res) => res.data);
  },

  getErrorHistory() {
    return api.get(`${BASE}/history/errored`).then((res) => res.data);
  },

  deleteHistoryEntry(id) {
    return api.delete(`${BASE}/history/${id}`).then((res) => res.data);
  },

  clearAllHistory() {
    return api.delete(`${BASE}/history`).then((res) => res.data);
  },

  // The History page wants "everything" — the backend only exposes
  // filtered lookups, so we fan out across every operation and merge.
  async getAllHistory() {
    const ops = ["add", "subtract", "divide", "convert", "compare"];
    const results = await Promise.allSettled(
      ops.map((op) => quantityService.getHistoryByOperation(op))
    );
    const rows = results
      .filter((r) => r.status === "fulfilled")
      .flatMap((r) => r.value);
    return rows.sort((a, b) => {
      const ta = a.createdAt ? new Date(a.createdAt).getTime() : 0;
      const tb = b.createdAt ? new Date(b.createdAt).getTime() : 0;
      if (tb !== ta) return tb - ta;
      return (b.id || 0) - (a.id || 0);
    });
  },
};
