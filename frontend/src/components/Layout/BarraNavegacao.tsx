import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  AppBar,
  Avatar,
  Badge,
  Box,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Toolbar,
  Tooltip,
  Typography,
} from '@mui/material';
import HowToVoteIcon from '@mui/icons-material/HowToVote';
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import SettingsOutlinedIcon from '@mui/icons-material/SettingsOutlined';
import LogoutIcon from '@mui/icons-material/Logout';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import { toast } from 'react-toastify';

const avisoMvp = (funcionalidade: string) =>
  toast.info(`${funcionalidade} não implementado(a) nesta versão (MVP).`);

interface BarraNavegacaoProps {
  ehMobile: boolean;
  aoAlternarDrawer: () => void;
}

const BarraNavegacao = ({ ehMobile, aoAlternarDrawer }: BarraNavegacaoProps) => {
  const navegar = useNavigate();

  const [ancorMenu, setAncorMenu] = useState<null | HTMLElement>(null);
  const menuAberto = Boolean(ancorMenu);

  const [drawerAberto, setDrawerAberto] = useState(false);

  const abrirMenu = (evento: React.MouseEvent<HTMLElement>) => {
    if (ehMobile) {
      setDrawerAberto(true);
    } else {
      setAncorMenu(evento.currentTarget);
    }
  };

  const fecharMenu = () => {
    setAncorMenu(null);
    setDrawerAberto(false);
  };

  const cabecalhoUtilizador = (
    <Box sx={{ px: 2.5, py: 2 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
        <Avatar
          sx={{
            width: 40,
            height: 40,
            bgcolor: 'primary.main',
            fontSize: '0.9rem',
            fontWeight: 700,
          }}
        >
          AF
        </Avatar>
        <Box>
          <Typography variant="subtitle2" fontWeight={600}>
            Adamastor Franca
          </Typography>
          <Typography variant="caption" color="text.secondary">
            admin@votacoop.com
          </Typography>
        </Box>
      </Box>
    </Box>
  );

  return (
    <AppBar
      position="fixed"
      elevation={0}
      sx={(tema) => ({
        zIndex: tema.zIndex.drawer + 1,
        backgroundColor: 'background.paper',
        borderBottom: '1px solid',
        borderColor: 'divider',
      })}
    >
      <Toolbar sx={{ justifyContent: 'space-between' }}>
        {}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          {ehMobile && (
            <IconButton
              onClick={aoAlternarDrawer}
              sx={{ color: 'text.secondary', mr: 0.5 }}
              aria-label="abrir menu"
            >
              <MenuIcon />
            </IconButton>
          )}
          <Box 
            onClick={() => navegar('/')}
            sx={{ display: 'flex', alignItems: 'center', gap: 1, cursor: 'pointer' }}
          >
            <HowToVoteIcon color="primary" sx={{ fontSize: 28 }} />
            <Typography
              variant="h6"
              noWrap
              sx={{ color: 'primary.main', fontWeight: 700 }}
            >
              VotaCoop
            </Typography>
          </Box>
        </Box>

        {}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          <Tooltip title="Notificações">
            <IconButton sx={{ color: 'text.secondary' }} onClick={() => avisoMvp('Notificações')}>
              <Badge badgeContent={3} color="error" variant="dot">
                <NotificationsNoneIcon />
              </Badge>
            </IconButton>
          </Tooltip>

          <Tooltip title="Menu do utilizador">
            <IconButton
              onClick={abrirMenu}
              size="small"
              aria-controls={menuAberto ? 'menu-utilizador' : undefined}
              aria-haspopup="true"
              aria-expanded={menuAberto ? 'true' : undefined}
              sx={{ ml: 0.5 }}
            >
              <Avatar
                sx={{
                  width: 36,
                  height: 36,
                  bgcolor: 'primary.main',
                  fontSize: '0.875rem',
                  fontWeight: 700,
                }}
              >
                AF
              </Avatar>
            </IconButton>
          </Tooltip>

          {}
          {!ehMobile && (
            <Menu
              id="menu-utilizador"
              anchorEl={ancorMenu}
              open={menuAberto}
              onClose={fecharMenu}
              onClick={fecharMenu}
              transformOrigin={{ horizontal: 'right', vertical: 'top' }}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
              slotProps={{
                paper: {
                  elevation: 3,
                  sx: {
                    mt: 1,
                    minWidth: 220,
                    borderRadius: 2,
                    overflow: 'visible',
                    '&::before': {
                      content: '""',
                      display: 'block',
                      position: 'absolute',
                      top: 0,
                      right: 14,
                      width: 10,
                      height: 10,
                      bgcolor: 'background.paper',
                      transform: 'translateY(-50%) rotate(45deg)',
                      zIndex: 0,
                      borderLeft: '1px solid',
                      borderTop: '1px solid',
                      borderColor: 'divider',
                    },
                  },
                },
              }}
            >
              <Box sx={{ px: 2, py: 1.5 }}>
                <Typography variant="subtitle2" fontWeight={600}>
                  Adamastor Franca
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  admin@votacoop.com
                </Typography>
              </Box>
              <Divider />
              <MenuItem onClick={() => avisoMvp('Meu Perfil')}>
                <ListItemIcon><PersonOutlineIcon fontSize="small" /></ListItemIcon>
                Meu Perfil
              </MenuItem>
              <MenuItem onClick={() => avisoMvp('Configurações')}>
                <ListItemIcon><SettingsOutlinedIcon fontSize="small" /></ListItemIcon>
                Configurações
              </MenuItem>
              <Divider />
              <MenuItem sx={{ color: 'error.main' }} onClick={() => avisoMvp('Logout')}>
                <ListItemIcon><LogoutIcon fontSize="small" sx={{ color: 'error.main' }} /></ListItemIcon>
                Sair
              </MenuItem>
            </Menu>
          )}

          {}
          {ehMobile && (
            <Drawer
              anchor="bottom"
              open={drawerAberto}
              onClose={fecharMenu}
              PaperProps={{
                sx: {
                  borderTopLeftRadius: 16,
                  borderTopRightRadius: 16,
                  maxHeight: '60vh',
                },
              }}
            >
              {}
              <Box sx={{ display: 'flex', justifyContent: 'center', pt: 1, pb: 0.5 }}>
                <Box sx={{ width: 36, height: 4, borderRadius: 2, bgcolor: 'divider' }} />
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', px: 1 }}>
                <IconButton size="small" onClick={fecharMenu}>
                  <CloseIcon fontSize="small" />
                </IconButton>
              </Box>

              {cabecalhoUtilizador}
              <Divider />

              <List disablePadding>
                <ListItemButton onClick={() => { avisoMvp('Meu Perfil'); fecharMenu(); }}>
                  <ListItemIcon><PersonOutlineIcon /></ListItemIcon>
                  <ListItemText primary="Meu Perfil" />
                </ListItemButton>
                <ListItemButton onClick={() => { avisoMvp('Configurações'); fecharMenu(); }}>
                  <ListItemIcon><SettingsOutlinedIcon /></ListItemIcon>
                  <ListItemText primary="Configurações" />
                </ListItemButton>
                <Divider />
                <ListItemButton onClick={() => { avisoMvp('Logout'); fecharMenu(); }} sx={{ color: 'error.main' }}>
                  <ListItemIcon><LogoutIcon sx={{ color: 'error.main' }} /></ListItemIcon>
                  <ListItemText primary="Sair" />
                </ListItemButton>
              </List>

              {}
              <Box sx={{ pb: 2 }} />
            </Drawer>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default BarraNavegacao;
