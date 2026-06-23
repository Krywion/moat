import { ApiRequestError, type ApiError } from '@/types/api'

export async function apiFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers)

  if (init.body && !(init.body instanceof FormData) && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  const response = await fetch(path, {
    ...init,
    headers,
    credentials: 'include',
  })

  if (response.status === 204) {
    return undefined as T
  }

  const contentType = response.headers.get('Content-Type') ?? ''
  const isJson = contentType.includes('application/json')
  const payload = isJson ? await response.json() : null

  if (!response.ok) {
    if (payload && typeof payload === 'object' && 'message' in payload) {
      throw new ApiRequestError(payload as ApiError)
    }

    throw new Error(`Request failed with status ${response.status}`)
  }

  return payload as T
}
