import DashboardIcon from '@mui/icons-material/Dashboard';
import HowToVoteIcon from '@mui/icons-material/HowToVote';

export const LARGURA_DRAWER = 260;

export const LARGURA_DRAWER_MINI = 72;

export const ALTURA_APPBAR = 64;

export interface ItemMenu {
  rotulo: string;
  icone: React.ReactNode;
  caminho: string;
}

export const itensMenu: ItemMenu[] = [
  { rotulo: 'Dashboard', icone: <DashboardIcon />, caminho: '/' },
  { rotulo: 'Pautas', icone: <HowToVoteIcon />, caminho: '/pautas' },
];
