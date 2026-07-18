import type { Page } from '@playwright/test';

/** Mapa sección de navegación → testid de la página destino */
export const SECTION_PAGE_TEST_IDS: Record<string, string> = {
  'Tomar pedidos': 'take-order-page',
  'Editar pedido': 'edit-order-page',
  'Vista pedidos': 'view-orders-page',
  'Gestión': 'management-page',
};

export async function navigateToSection(page: Page, section: string): Promise<void> {
  const pageTestId = SECTION_PAGE_TEST_IDS[section];
  if (!pageTestId) {
    throw new Error(`Sección de navegación desconocida: ${section}`);
  }

  const bottomNavButton = page.getByRole('button', { name: section });
  if (await bottomNavButton.isVisible()) {
    await bottomNavButton.click();
  } else {
    await page.locator('app-toolbar button[mat-icon-button]').first().click();
    await page.locator('mat-list-item').filter({ hasText: section }).click();
  }

  await page.getByTestId(pageTestId).waitFor({ state: 'visible' });
}
