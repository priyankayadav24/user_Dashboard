import { useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';

const FIELDS = [
  { name: 'firstName', label: 'First name', type: 'text' },
  { name: 'lastName', label: 'Last name', type: 'text' },
  { name: 'email', label: 'Email', type: 'email' },
  { name: 'mobileNumber', label: 'Mobile number', type: 'tel' },
  { name: 'password', label: 'Choose password', type: 'password' },
];

const EMPTY = { firstName: '', lastName: '', email: '', mobileNumber: '', password: '' };

export default function RegisterPage() {
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState(null);
  const [done, setDone] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const update = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await api.register(form);
      setDone(true);
    } catch (err) {
      // Validation errors come back per field; show them all, otherwise the server message.
      const messages = err.fieldErrors ? Object.values(err.fieldErrors) : [err.message];
      setError(messages.join('. '));
    } finally {
      setSubmitting(false);
    }
  }

  if (done) {
    return (
      <section className="card">
        <h2>Registration successful</h2>
        <p>Your account is ready.</p>
        <Link to="/login">Go to login</Link>
      </section>
    );
  }

  return (
    <section className="card">
      <h2>Register</h2>
      <form onSubmit={handleSubmit} noValidate>
        {FIELDS.map(({ name, label, type }) => (
          <label key={name}>
            {label}
            <input type={type} value={form[name]} onChange={update(name)} required />
          </label>
        ))}
        <button type="submit" disabled={submitting}>
          {submitting ? 'Submitting...' : 'Submit'}
        </button>
      </form>

      {error && <p className="error">{error}</p>}
      <p className="muted">
        Already registered? <Link to="/login">Login</Link>
      </p>
    </section>
  );
}
