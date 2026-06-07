const BASE_URL = process.env.BASE_URL || 'http://localhost';
const API_URL = process.env.API_BASE_URL || 'http://localhost:8080';
const MAX_ATTEMPTS = 30;
const DELAY_MS = 2000;

async function waitForUrl(url: string, label: string): Promise<void> {
  for (let attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
    try {
      const response = await fetch(url);
      if (response.ok) {
        console.log(`[global-setup] ${label} listo (${url})`);
        return;
      }
    } catch {
      // reintento
    }
    console.log(`[global-setup] Esperando ${label} (${attempt}/${MAX_ATTEMPTS})...`);
    await new Promise((resolve) => setTimeout(resolve, DELAY_MS));
  }
  throw new Error(`[global-setup] ${label} no respondió a tiempo: ${url}`);
}

export default async function globalSetup(): Promise<void> {
  await waitForUrl(`${BASE_URL}/`, 'Frontend');
  await waitForUrl(`${API_URL}/actuator/health`, 'Backend');
}
