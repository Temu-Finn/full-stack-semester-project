type LogLevel = 'error' | 'warn' | 'info' | 'debug'

class Logger {
  private formatMessage(level: LogLevel, message: string, data?: unknown): string {
    const timestamp = new Date().toISOString()
    const dataString = data ? `\nData: ${JSON.stringify(data, null, 2)}` : ''
    return `[${timestamp}] ${level.toUpperCase()}: ${message}${dataString}`
  }

  error(message: string, data?: unknown): void {
    console.error(this.formatMessage('error', message, data))
  }

  warn(message: string, data?: unknown): void {
    console.warn(this.formatMessage('warn', message, data))
  }

  info(message: string, data?: unknown): void {
    console.info(this.formatMessage('info', message, data))
  }

  debug(message: string, data?: unknown): void {
    if (import.meta.env.DEV) {
      console.log(this.formatMessage('debug', message, data))
    }
  }
}

export const logger = new Logger()
