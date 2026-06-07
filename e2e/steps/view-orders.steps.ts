import { expect } from '@playwright/test';
import { createBdd } from 'playwright-bdd';
import { navigateToSection } from '../support/navigation';

const { When, Then } = createBdd();

When('abro la vista de pedidos', async ({ page }) => {
  await navigateToSection(page, 'Vista pedidos');
  await expect(page.getByTestId('view-orders-grid')).toBeVisible({ timeout: 15_000 });
});

Then('veo el pedido de mesa {string}', async ({ page }, table: string) => {
  const card = page.getByTestId(`order-card-${table}`);
  await expect.poll(async () => card.isVisible(), { timeout: 20_000, intervals: [500, 1000] }).toBe(true);
});

When('marco como entregado el pedido de mesa {string}', async ({ page }, table: string) => {
  await page.getByTestId(`order-card-${table}`).click();
});

Then('el pedido de mesa {string} muestra estado entregado', async ({ page }, table: string) => {
  const card = page.getByTestId(`order-card-${table}`);
  await expect(card.getByText('Entregado')).toBeVisible();
});
