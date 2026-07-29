import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Dev server is pinned to port 3000 on purpose: the existing Spring Boot
// SecurityConfig.corsConfigurationSource() already whitelists
// http://localhost:3000, so running Vite on this port means ZERO backend
// changes are needed for CORS. Change here + in SecurityConfig together if
// you ever need a different port.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    strictPort: true,
  },
  preview: {
    port: 3000,
    strictPort: true,
  },
});
