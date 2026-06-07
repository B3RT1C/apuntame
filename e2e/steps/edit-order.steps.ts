import { expect } from '@playwright/test';
import { createBdd } from 'playwright-bdd';
import { navigateToSection } from '../support/navigation';

const { When, Then } = createBdd();

When('abro editar pedido', async ({ page }) => {
  await navigateToSection(page, 'Editar pedido');
});

When('selecciono el pedido de mesa {string}', async ({ page }, table: string) => {
  await page.getByTestId(`order-row-${table}`).click();
  await expect(page.getByRole('dialog')).toBeVisible();
});

When('elimino todos los productos del dialogo de edicion', async ({ page }) => {
  const dialog = page.getByRole('dialog');
  const removeBtn = dialog.getByTestId('remove-item-btn');
  while (await removeBtn.count()) {
    await removeBtn.first().click();
  }
});

When('confirmo los cambios del pedido', async ({ page }) => {
  await page.getByTestId('edit-order-confirm-btn').click();
  await expect(page.getByRole('dialog')).not.toBeVisible();
});

Then('los cambios del pedido se guardaron correctamente', async ({ page }) => {
  await expect(page.getByTestId('edit-order-page')).toBeVisible();
  await expect(page.getByRole('dialog')).not.toBeVisible();
});
