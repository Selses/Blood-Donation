import React, { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Lenis from 'lenis';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import BloodRequests from './pages/BloodRequests';
import BloodRequestForm from './pages/BloodRequestForm';
import BloodRequestDetails from './pages/BloodRequestDetails';
import Notifications from './pages/Notifications';
import Inventory from './pages/Inventory';

// Smooth Scroll & Route Reset Helper
function SmoothScrollManager({ children }) {
  const location = useLocation();

  useEffect(() => {
    const lenis = new Lenis({
      duration: 1.2,
      easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)),
      orientation: 'vertical',
      gestureOrientation: 'vertical',
      smoothWheel: true,
      wheelMultiplier: 1.1,
      touchMultiplier: 1.5,
      infinite: false
    });

    let animationFrameId;
    function raf(time) {
      lenis.raf(time);
      animationFrameId = requestAnimationFrame(raf);
    }
    animationFrameId = requestAnimationFrame(raf);

    // Scroll to top smoothly on navigation
    lenis.scrollTo(0, { immediate: true });

    return () => {
      cancelAnimationFrame(animationFrameId);
      lenis.destroy();
    };
  }, [location.pathname]);

  return children;
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <SmoothScrollManager>
          <div className="app-layout">
            <Navbar />
            <main className="main-content">
            <Routes>
              {/* Public Landing & Auth Routes */}
              <Route path="/" element={<Home />} />
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
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </SmoothScrollManager>
    </BrowserRouter>
  </AuthProvider>
  );
}

export default App;
