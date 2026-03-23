import { Chip, type ChipProps } from '@mui/material';

export type TipoBadge = 'sucesso' | 'erro' | 'alerta' | 'info' | 'neutro' | 'padrao';

interface BadgeCorporativoProps extends Omit<ChipProps, 'color'> {
  tipo: TipoBadge;
}

const getCoresPorTipo = (tipo: TipoBadge) => {
  switch (tipo) {
    case 'sucesso':
      return { bgcolor: '#e8f5e9', color: '#2e7d32' };
    case 'erro':
      return { bgcolor: '#ffebee', color: '#c62828' };
    case 'alerta':
      return { bgcolor: '#fff3e0', color: '#ef6c00' };
    case 'info':
      return { bgcolor: '#e3f2fd', color: '#1565c0' };
    case 'neutro':
      return { bgcolor: '#f5f5f5', color: '#757575' };
    case 'padrao':
    default:
      return {};
  }
};

const BadgeCorporativo = ({ tipo, size = 'small', sx, ...props }: BadgeCorporativoProps) => {
  const cores = getCoresPorTipo(tipo);
  const isOutlined = props.variant === 'outlined';

  return (
    <Chip
      size={size}
      {...props}
      sx={{
        fontWeight: 600,
        height: size === 'small' ? 26 : 32, 
        fontSize: '0.75rem',
        ...(isOutlined 
           ? { borderColor: cores.color, color: cores.color }
           : cores
        ),
        '& .MuiChip-icon': {
          color: 'inherit',
        },
        ...sx,
      }}
    />
  );
};

export default BadgeCorporativo;
