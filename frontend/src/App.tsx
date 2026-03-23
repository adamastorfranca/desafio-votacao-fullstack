import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import tema from './styles/tema';
import LayoutPrincipal from './components/Layout/LayoutPrincipal';
import PainelPrincipal from './pages/PainelPrincipal';
import PaginaPautas from './pages/PaginaPautas';

function App() {
  return (
    <ThemeProvider theme={tema}>
      <CssBaseline />
      <BrowserRouter>
        <LayoutPrincipal>
          <Routes>
            {}
            <Route path="/" element={<PainelPrincipal />} />

            {}
            <Route path="/pautas" element={<PaginaPautas />} />

            {}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </LayoutPrincipal>
      </BrowserRouter>
      <ToastContainer
        position="top-right"
        autoClose={4000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="colored"
      />
    </ThemeProvider>
  );
}

export default App;
