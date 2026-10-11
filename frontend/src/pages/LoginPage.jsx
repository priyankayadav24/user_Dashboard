import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState(null);
  const [notRegistered, setNotRegistered] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const update = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setNotRegistered(false);
    setSubmitting(true);
    try {
      await login(form.email, form.password);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.message);
      setNotRegistered(err.status === 404);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="card">
      <h2>Login</h2>
      <form onSubmit={handleSubmit} noValidate>
        <label>
          Email
          <input type="email" value={form.email} onChange={update('email')} required />
        </label>
        <label>
          Password
          <input type="password" value={form.password} onChange={update('password')} required />
        </label>
        <button type="submit" disabled={submitting}>
          {submitting ? 'Logging in...' : 'Submit'}
        </button>
      </form>

      {error && (
        <p className="error">
          {error}
          {notRegistered && (
            <>
              {' '}
              <Link to="/register">Register here</Link>
            </>
          )}
        </p>
      )}
    </section>
  );
}
