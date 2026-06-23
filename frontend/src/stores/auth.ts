import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import * as authApi from '@/api/auth'
import { ApiRequestError, type UserResponse } from '@/types/api'

export type AuthStatus = 'idle' | 'loading' | 'authenticated' | 'unauthenticated'

let initializePromise: Promise<void> | null = null

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserResponse | null>(null)
  const status = ref<AuthStatus>('idle')

  const isAuthenticated = computed(() => status.value === 'authenticated')

  async function initialize(): Promise<void> {
    if (status.value !== 'idle') {
      return
    }

    if (!initializePromise) {
      initializePromise = (async () => {
        status.value = 'loading'
        try {
          user.value = await authApi.fetchMe()
          status.value = 'authenticated'
        } catch {
          user.value = null
          status.value = 'unauthenticated'
        }
      })()
    }

    await initializePromise
  }

  async function login(email: string, password: string): Promise<void> {
    status.value = 'loading'
    try {
      user.value = await authApi.login({ email, password })
      status.value = 'authenticated'
    } catch (error) {
      user.value = null
      status.value = 'unauthenticated'
      throw error
    }
  }

  async function register(email: string, password: string): Promise<UserResponse> {
    return authApi.register({ email, password })
  }

  async function logout(): Promise<void> {
    status.value = 'loading'
    try {
      await authApi.logout()
    } finally {
      user.value = null
      status.value = 'unauthenticated'
    }
  }

  function getErrorMessage(error: unknown): string {
    if (error instanceof ApiRequestError) {
      return error.message
    }

    if (error instanceof Error) {
      return error.message
    }

    return 'Wystąpił nieoczekiwany błąd.'
  }

  return {
    user,
    status,
    isAuthenticated,
    initialize,
    login,
    register,
    logout,
    getErrorMessage,
  }
})
