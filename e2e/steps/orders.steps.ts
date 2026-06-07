import { expect } from '@playwright/test';
import { createBdd } from 'playwright-bdd';
import { navigateToSection } from '../support/navigation';

const { When, Then } = createBdd();

When('voy a {string}', async ({ page }, section: string) => {
  await navigateToSection(page, section);
});

When('escribo mesa {string}', async ({ page }, tableNumber: string) => {
  await page.getByTestId('table-input').fill(tableNumber);
});

When('añado el articulo {string} al pedido', async ({ page }, itemName: string) => {
  await page.getByTestId(`item-card-${itemName}`).click();
  await expect(page.locator('app-order-summary')).toContainText(itemName);
});

When('envio el pedido', async ({ page }) => {
  await page.getByTestId('send-order-btn').click();
});

Then('el pedido actual queda vacio', async ({ page }) => {
  await expect(page.getByTestId('send-order-btn')).toBeDisabled();
});
