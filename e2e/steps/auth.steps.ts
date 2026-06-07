import { expect } from '@playwright/test';
import { createBdd } from 'playwright-bdd';

const { Given, When, Then } = createBdd();

const DEMO_PASSWORDS: Record<string, string> = {
  admin: 'admin',
  camarero1: 'camarero1',
  camarero2: 'camarero2',
};

Given('estoy en la pagina de login', async ({ page }) => {
  await page.goto('/login');
  await expect(page.getByTestId('login-form')).toBeVisible();
});

When(
  'introduzco usuario {string} y contraseña {string}',
  async ({ page }, username: string, password: string) => {
    await page.getByTestId('login-username').fill(username);
    await page.getByTestId('login-password').fill(password);
  },
);

When('pulso {string}', async ({ page }, buttonName: string) => {
  if (buttonName === 'Iniciar Sesión') {
    await page.getByTestId('login-submit').click();
  } else {
    await page.getByRole('button', { name: buttonName, exact: true }).click();
  }
});

Then('veo la pagina de inicio', async ({ page }) => {
  await expect(page.getByTestId('take-order-page')).toBeVisible();
});

Then('veo el mensaje de error de login', async ({ page }) => {
  await expect(page.locator('.error-message')).toBeVisible();
  await expect(page.locator('app-login')).toBeVisible();
});

Given('he iniciado sesion como {string}', async ({ page }, username: string) => {
  const password = DEMO_PASSWORDS[username] ?? username;
  await page.goto('/login');
  await page.getByTestId('login-username').fill(username);
  await page.getByTestId('login-password').fill(password);
  await page.getByTestId('login-submit').click();
  await expect(page.getByTestId('take-order-page')).toBeVisible();
});
