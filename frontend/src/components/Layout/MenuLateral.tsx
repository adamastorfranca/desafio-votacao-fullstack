import {
  Box,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Tooltip,
  useTheme,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import KeyboardDoubleArrowLeftIcon from '@mui/icons-material/KeyboardDoubleArrowLeft';
import KeyboardDoubleArrowRightIcon from '@mui/icons-material/KeyboardDoubleArrowRight';
import { useNavigate, useLocation } from 'react-router-dom';

import {
  LARGURA_DRAWER,
  LARGURA_DRAWER_MINI,
  ALTURA_APPBAR,
  itensMenu,
} from './constantesLayout';

interface MenuLateralProps {

  ehMobile: boolean;

  drawerExpandido: boolean;

  drawerMobileAberto: boolean;

  aoAlternarDrawer: () => void;

  aoFecharDrawerMobile: () => void;
}

const MenuLateral = ({
  ehMobile,
  drawerExpandido,
  drawerMobileAberto,
  aoAlternarDrawer,
  aoFecharDrawerMobile,
}: MenuLateralProps) => {
  const tema = useTheme();
  const navegar = useNavigate();
  const localizacao = useLocation();

  const larguraAtual = drawerExpandido ? LARGURA_DRAWER : LARGURA_DRAWER_MINI;

  const renderizarItens = (expandido: boolean) => (
    <List sx={{ px: expandido ? 1.5 : 1, pt: 1, flex: 1 }}>
      {itensMenu.map((item) => {
        const estaSelecionado = localizacao.pathname === item.caminho;

        return (
          <Tooltip
            key={item.caminho}
            title={expandido ? '' : item.rotulo}
            placement="right"
            arrow
          >
            <ListItemButton
              selected={estaSelecionado}
              onClick={() => {
                navegar(item.caminho);
                if (ehMobile) aoFecharDrawerMobile();
              }}
              sx={{
                borderRadius: 2,
                mb: 0.5,
                justifyContent: expandido ? 'initial' : 'center',
                px: expandido ? 2 : 1.5,
                '&.Mui-selected': {
                  backgroundColor: 'primary.main',
                  color: 'primary.contrastText',
                  '&:hover': { backgroundColor: 'primary.dark' },
                  '& .MuiListItemIcon-root': { color: 'primary.contrastText' },
                },
                '&:hover': {
                  backgroundColor: 'action.hover',
                },
              }}
            >
              <ListItemIcon
                sx={{
                  minWidth: expandido ? 40 : 'auto',
                  color: estaSelecionado ? 'inherit' : 'text.secondary',
                }}
              >
                {item.icone}
              </ListItemIcon>
              {expandido && (
                <ListItemText
                  primary={item.rotulo}
                  primaryTypographyProps={{ fontWeight: estaSelecionado ? 600 : 400 }}
                />
              )}
            </ListItemButton>
          </Tooltip>
        );
      })}
    </List>
  );

  const botaoColapsar = (expandido: boolean) => (
    <Box
      sx={{
        p: 1.5,
        borderTop: '1px solid',
        borderColor: 'divider',
        display: 'flex',
        justifyContent: expandido ? 'flex-end' : 'center',
      }}
    >
      <Tooltip
        title={expandido ? 'Recolher menu' : 'Expandir menu'}
        placement="right"
        arrow
      >
        <IconButton
          onClick={aoAlternarDrawer}
          size="small"
          sx={{
            width: 36,
            height: 36,
            borderRadius: '50%',
            backgroundColor: 'action.hover',
            color: 'text.secondary',
            transition: 'all 0.2s ease',
            '&:hover': {
              backgroundColor: 'primary.main',
              color: 'primary.contrastText',
              transform: 'scale(1.1)',
            },
          }}
        >
          {expandido ? (
            <KeyboardDoubleArrowLeftIcon fontSize="small" />
          ) : (
            <KeyboardDoubleArrowRightIcon fontSize="small" />
          )}
        </IconButton>
      </Tooltip>
    </Box>
  );

  const cabecalhoMobile = (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        height: 48,
        borderBottom: '1px solid',
        borderColor: 'divider',
      }}
    >
      <IconButton onClick={aoFecharDrawerMobile} sx={{ color: 'text.secondary' }}>
        <MenuIcon />
      </IconButton>
    </Box>
  );

  const conteudoDrawer = (expandido: boolean, incluirBotaoColapsar: boolean) => (
    <Box
      sx={{
        overflow: 'auto',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      {renderizarItens(expandido)}
      {incluirBotaoColapsar && botaoColapsar(expandido)}
    </Box>
  );

  const estilosPaperComuns = {
    boxSizing: 'border-box' as const,
    borderRight: '1px solid',
    borderColor: 'divider',
    backgroundColor: 'background.paper',
    mt: `${ALTURA_APPBAR}px`,
    height: `calc(100% - ${ALTURA_APPBAR}px)`,
  };

  const transicaoLargura = tema.transitions.create('width', {
    easing: tema.transitions.easing.sharp,
    duration: tema.transitions.duration.enteringScreen,
  });

  if (ehMobile) {
    return (
      <Drawer
        variant="temporary"
        open={drawerMobileAberto}
        onClose={aoFecharDrawerMobile}
        ModalProps={{ keepMounted: true }}
        sx={{
          '& .MuiDrawer-paper': {
            ...estilosPaperComuns,
            width: LARGURA_DRAWER,
          },
        }}
      >
        {cabecalhoMobile}
        {conteudoDrawer(true, false)}
      </Drawer>
    );
  }

  return (
    <Drawer
      variant="permanent"
      open
      sx={{
        width: larguraAtual,
        flexShrink: 0,
        transition: transicaoLargura,
        '& .MuiDrawer-paper': {
          ...estilosPaperComuns,
          width: larguraAtual,
          overflowX: 'hidden',
          transition: transicaoLargura,
        },
      }}
    >
      {conteudoDrawer(drawerExpandido, true)}
    </Drawer>
  );
};

export default MenuLateral;
