import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
      reporters: ['default', 'json'],
      outputFile: {
        json: './test-results/vitest-report.json',
      },
      coverage: {
        reporter: ['text', 'json', 'html'],
        reportsDirectory: './coverage',
      },
    },
  }),
)
