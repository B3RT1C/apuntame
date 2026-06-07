import { expect } from '@playwright/test';
import { createBdd } from 'playwright-bdd';

const { Given } = createBdd();

const API_BASE = process.env.API_BASE_URL || 'http://localhost:8080/api';

async function getAuthToken(
  request: import('@playwright/test').APIRequestContext,
  username: string,
  password: string,
): Promise<string> {
  const response = await request.post(`${API_BASE}/auth/login`, {
    data: { username, password },
  });
  const body = await response.json();
  return body.token;
}

Given(
  'existe un pedido pendiente en mesa {string} con {string}',
  async ({ request }, table: string, itemName: string) => {
    const token = await getAuthToken(request, 'camarero1', 'camarero1');

    const itemsResponse = await request.get(`${API_BASE}/items`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    const items = await itemsResponse.json();
    const item = items.find((i: { name: string }) => i.name === itemName);
    if (!item) {
      throw new Error(`Producto no encontrado: ${itemName}`);
    }

    const createResponse = await request.post(`${API_BASE}/orders`, {
      headers: { Authorization: `Bearer ${token}` },
      data: {
        table,
        takenBy: { username: 'camarero1' },
        orderItems: [{ item: { id: item.id }, amount: 1 }],
      },
    });
    expect(createResponse.ok()).toBeTruthy();

    await expect(async () => {
      const listResponse = await request.get(`${API_BASE}/orders`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const body = await listResponse.json();
      const found = body.orders?.some((o: { table: string }) => o.table === table);
      expect(found).toBeTruthy();
    }).toPass({ timeout: 15_000, intervals: [500, 1000] });
  },
);
