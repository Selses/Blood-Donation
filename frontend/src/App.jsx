import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import BloodRequests from './pages/BloodRequests';
import BloodRequestForm from './pages/BloodRequestForm';
import BloodRequestDetails from './pages/BloodRequestDetails';
import Notifications from './pages/Notifications';
import Inventory from './pages/Inventory';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="app-layout">
          <Navbar />
          <main className="main-content">
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Protected Routes (All Authenticated Users) */}
              <Route element={<ProtectedRoute />}>
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/blood-requests" element={<BloodRequests />} />
                <Route path="/blood-requests/:id" element={<BloodRequestDetails />} />
                <Route path="/notifications" element={<Notifications />} />
              </Route>

              {/* Protected Routes (Recipient, Hospital, Admin) */}
              <Route element={<ProtectedRoute allowedRoles={['RECIPIENT', 'HOSPITAL', 'ADMIN']} />}>
                <Route path="/blood-requests/new" element={<BloodRequestForm />} />
              </Route>

              {/* Protected Routes (Hospital, Blood Bank, Admin) */}
              <Route element={<ProtectedRoute allowedRoles={['HOSPITAL', 'BLOOD_BANK', 'ADMIN']} />}>
                <Route path="/inventory" element={<Inventory />} />
              </Route>

              {/* Fallback Redirect */}
              <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
