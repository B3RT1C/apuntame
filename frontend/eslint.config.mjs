import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import json from "@eslint/json";
import markdown from "@eslint/markdown";
import css from "@eslint/css";

export default [
  // Configuración base para archivos JavaScript y TypeScript
  {
    files: ["**/*.{js,mjs,cjs,ts,mts,cts}"],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.node,
      },
      parserOptions: {
        ecmaVersion: 2022,
        sourceType: "module",
      },
    },
  },

  // Configuración recomendada de ESLint para JavaScript
  js.configs.recommended,

  // Configuración recomendada de TypeScript ESLint
  ...tseslint.configs.recommended,

  // Reglas específicas para Angular y TypeScript
  {
    files: ["**/*.ts"],
    rules: {
      // Reglas de TypeScript
      "@typescript-eslint/no-explicit-any": "warn",
      "@typescript-eslint/explicit-function-return-type": "off",
      "@typescript-eslint/explicit-module-boundary-types": "off",
      "@typescript-eslint/no-unused-vars": ["error", {
        "argsIgnorePattern": "^_",
        "varsIgnorePattern": "^_"
      }],
      "@typescript-eslint/no-inferrable-types": "off",

      // Reglas generales
      "no-console": ["warn", { "allow": ["warn", "error"] }],
      "prefer-const": "error",
      "no-var": "error",
      "eqeqeq": ["error", "always"],
      "curly": ["error", "all"],

      // Reglas para RxJS (útil para WebSockets y observables)
      "no-async-promise-executor": "error",
      "require-atomic-updates": "error",
    },
  },

  // Configuración para archivos JSON
  {
    files: ["**/*.json"],
    plugins: {
      json,
    },
    language: "json/json",
    rules: {
      ...json.configs.recommended.rules,
    },
  },

  // Configuración para archivos Markdown
  {
    files: ["**/*.md"],
    plugins: {
      markdown,
    },
    language: "markdown/gfm",
    rules: {
      ...markdown.configs.recommended.rules,
    },
  },

  // Configuración para archivos CSS
  {
    files: ["**/*.css"],
    plugins: {
      css,
    },
    language: "css/css",
    rules: {
      ...css.configs.recommended.rules,
    },
  },

  // Ignorar directorios comunes
  {
    ignores: [
      "node_modules/**",
      "dist/**",
      ".angular/**",
      "coverage/**",
      "*.config.js",
      "karma.conf.js",
    ],
  },
];
