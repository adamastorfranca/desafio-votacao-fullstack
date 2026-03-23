import { useState, type ReactNode } from 'react';
import { Box, useMediaQuery, useTheme } from '@mui/material';

import BarraNavegacao from './BarraNavegacao';
import MenuLateral from './MenuLateral';
import {
  LARGURA_DRAWER,
  LARGURA_DRAWER_MINI,
  ALTURA_APPBAR,
} from './constantesLayout';

interface LayoutPrincipalProps {

  children: ReactNode;
}

export default function LayoutPrincipal({ children }: LayoutPrincipalProps) {
  const tema = useTheme();
  const ehMobile = useMediaQuery(tema.breakpoints.down('md'));

  const [drawerExpandido, setDrawerExpandido] = useState(true);
  const [drawerMobileAberto, setDrawerMobileAberto] = useState(false);

  const alternarDrawer = () => {
    if (ehMobile) {
      setDrawerMobileAberto((prev) => !prev);
    } else {
      setDrawerExpandido((prev) => !prev);
    }
  };

  const larguraDrawer = ehMobile
    ? 0
    : drawerExpandido
      ? LARGURA_DRAWER
      : LARGURA_DRAWER_MINI;

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      {}
      <BarraNavegacao ehMobile={ehMobile} aoAlternarDrawer={alternarDrawer} />

      {}
      <MenuLateral
        ehMobile={ehMobile}
        drawerExpandido={drawerExpandido}
        drawerMobileAberto={drawerMobileAberto}
        aoAlternarDrawer={alternarDrawer}
        aoFecharDrawerMobile={() => setDrawerMobileAberto(false)}
      />

      {}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: { xs: '100%', md: `calc(100% - ${larguraDrawer}px)` },
          mt: `${ALTURA_APPBAR}px`,
          backgroundColor: 'background.default',
          minHeight: `calc(100vh - ${ALTURA_APPBAR}px)`,
          transition: tema.transitions.create(['width', 'margin'], {
            easing: tema.transitions.easing.sharp,
            duration: tema.transitions.duration.enteringScreen,
          }),
        }}
      >
        {children}
      </Box>
    </Box>
  );
}
