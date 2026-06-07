import { expect } from '@playwright/test';
import { createBdd } from 'playwright-bdd';

const { When, Then } = createBdd();

let lastCreatedCategoryName = '';

When('abro la pestaña {string}', async ({ page }, tabName: string) => {
  await page.getByRole('tab', { name: tabName }).click();
});

When('creo una categoria llamada {string}', async ({ page }, categoryName: string) => {
  await page.getByTestId('create-category-btn').click();
  await page.getByTestId('category-name-input').fill(categoryName);
  await page.getByTestId('category-submit-btn').click();
});

When('creo una categoria con nombre unico', async ({ page }) => {
  lastCreatedCategoryName = `E2E-Cat-${Date.now()}`;
  await page.getByTestId('create-category-btn').click();
  await page.getByTestId('category-name-input').fill(lastCreatedCategoryName);
  await page.getByTestId('category-submit-btn').click();
});

Then('veo el mensaje {string}', async ({ page }, message: string) => {
  await expect(page.getByText(message)).toBeVisible();
});

Then('veo la categoria {string} en la tabla', async ({ page }, categoryName: string) => {
  await expect(page.getByTestId('management-page')).toContainText(categoryName);
});

Then('veo la categoria creada en la tabla', async ({ page }) => {
  await expect(page.getByTestId('management-page')).toContainText(lastCreatedCategoryName);
});
