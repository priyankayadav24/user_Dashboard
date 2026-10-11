const SESSION_KEY = 'userPortal.session';

/** Error carrying the HTTP status and, for validation failures, per-field messages. */
export class ApiError extends Error {
  constructor(message, status, fieldErrors) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request(path, { method = 'GET', body, token } = {}) {
  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (token) headers.Authorization = `Bearer ${token}`;

  const res = await fetch(path, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const isJson = res.headers.get('content-type')?.includes('application/json');
  const data = isJson ? await res.json() : null;

  if (!res.ok) {
    const fallback = res.status === 401 ? 'Session expired, please login again' : `Request failed (${res.status})`;
    throw new ApiError(data?.message ?? fallback, res.status, data?.fieldErrors);
  }
  return data;
}

export const api = {
  login: (email, password) =>
    request('/api/auth/login', { method: 'POST', body: { email, password } }),

  register: (form) =>
    request('/api/auth/register', { method: 'POST', body: form }),

  existingUsers: (token) =>
    request('/api/users/existing', { token }),

  async downloadUsersCsv(token) {
    const res = await fetch('/api/users/export', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new ApiError('Download failed', res.status);
    return res.blob();
  },
};

// Session is kept in sessionStorage: it survives page refreshes but not closing the tab.
export function loadSession() {
  try {
    return JSON.parse(sessionStorage.getItem(SESSION_KEY));
  } catch {
    return null;
  }
}

export function saveSession(session) {
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

export function clearSession() {
  sessionStorage.removeItem(SESSION_KEY);
}
