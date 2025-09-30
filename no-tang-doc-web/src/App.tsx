// src/App.tsx
import { useState, useEffect } from 'react';
import { AuthPage } from './components/AuthPage';
import { Dashboard } from './components/Dashboard';
import { UploadPage } from './components/UploadPage';
import { AuthProvider, useAuth } from './components/AuthContext';
import { ThemeProvider } from './components/ThemeContext';
import { Toaster } from './components/ui/sonner';
import { HomePage } from './pages/HomePage';

type Page = 'landing' | 'auth' | 'dashboard' | 'upload';
type AuthMode = 'login' | 'register';

function AppContent() {
  const { user } = useAuth();
  const [currentPage, setCurrentPage] = useState<Page>('landing');
  const [authMode, setAuthMode] = useState<AuthMode>('login');
  const [pendingUpload, setPendingUpload] = useState(false);

  useEffect(() => {
    if (user && pendingUpload) {
      setCurrentPage('upload');
      setPendingUpload(false);
    } else if (user && currentPage === 'auth') {
      setCurrentPage('dashboard');
    }
  }, [user, pendingUpload, currentPage]);

  const gotoAuth = (mode: AuthMode) => {
    setAuthMode(mode);
    setCurrentPage('auth');
  };
  const gotoDashboard = () => setCurrentPage('dashboard');
  const gotoLanding = () => setCurrentPage('landing');
  const gotoUpload = () => {
    if (user) {
      setCurrentPage('upload');
    } else {
      setPendingUpload(true);
      setAuthMode('login');
      setCurrentPage('auth');
    }
  };

  if (currentPage === 'upload' && user) {
    return <UploadPage onBack={gotoDashboard} onNavigateHome={gotoLanding} />;
  }
  if (currentPage === 'dashboard' && user) {
    return <Dashboard onNavigateHome={gotoLanding} onNavigateToUpload={gotoUpload} />;
  }
  if (currentPage === 'auth') {
    return <AuthPage initialMode={authMode} onBack={gotoLanding} />;
  }
  return (
      <HomePage
          onNavigateToAuth={gotoAuth}
          onNavigateToDashboard={gotoDashboard}
          onNavigateHome={gotoLanding}
          onStartUploading={gotoUpload}
      />
  );
}

export default function App() {
  return (
      <ThemeProvider>
        <AuthProvider>
          <AppContent />
          <Toaster />
        </AuthProvider>
      </ThemeProvider>
  );
}
