export interface UserResponse {
  id: string
  email: string
  role: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
}

export class ApiRequestError extends Error {
  readonly status: number
  readonly error: string
  readonly timestamp: string

  constructor(payload: ApiError) {
    super(payload.message)
    this.name = 'ApiRequestError'
    this.status = payload.status
    this.error = payload.error
    this.timestamp = payload.timestamp
  }
}
