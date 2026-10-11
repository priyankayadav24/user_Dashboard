import { useState } from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import { api } from '../api.js';

export default function DashboardPage() {
  const { token, user, message, logout } = useAuth();
  const [users, setUsers] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const isAdmin = user.userType === 'A';

  function handleError(err) {
    // An expired or invalid token means the session is no longer usable.
    if (err.status === 401) {
      logout();
      return;
    }
    setError(err.message);
  }

  async function showExistingUsers() {
    setError(null);
    setLoading(true);
    try {
      setUsers(await api.existingUsers(token));
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  }

  async function downloadUsers() {
    setError(null);
    try {
      const blob = await api.downloadUsersCsv(token);
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'users.csv';
      link.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      handleError(err);
    }
  }

  return (
    <section className="card wide">
      <div className="row between">
        <h2>{message}</h2>
        <button className="secondary" onClick={logout}>Logout</button>
      </div>
      <p className="muted">
        Signed in as {user.firstName} {user.lastName} ({isAdmin ? 'Admin' : 'User'})
      </p>

      <div className="row">
        <button onClick={showExistingUsers} disabled={loading}>
          {loading ? 'Loading...' : 'Show Existing User'}
        </button>
        {isAdmin && (
          <button className="secondary" onClick={downloadUsers}>
            Download Users (CSV)
          </button>
        )}
      </div>

      {error && <p className="error">{error}</p>}
      {users && <UsersTable users={users} />}
    </section>
  );
}

function UsersTable({ users }) {
  if (users.length === 0) {
    return <p className="muted">No users found.</p>;
  }
  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>First name</th>
            <th>Last name</th>
            <th>Email</th>
            <th>Mobile number</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.firstName}</td>
              <td>{u.lastName}</td>
              <td>{u.email}</td>
              <td>{u.mobileNumber}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
