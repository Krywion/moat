import { apiFetch } from '@/api/client'
import type { LoginRequest, RegisterRequest, UserResponse } from '@/types/api'

export function login(request: LoginRequest): Promise<UserResponse> {
  return apiFetch<UserResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function register(request: RegisterRequest): Promise<UserResponse> {
  return apiFetch<UserResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function logout(): Promise<void> {
  return apiFetch<void>('/auth/logout', {
    method: 'POST',
  })
}

export function fetchMe(): Promise<UserResponse> {
  return apiFetch<UserResponse>('/auth/me')
}
