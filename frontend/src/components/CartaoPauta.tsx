import { Card, CardContent, Typography } from '@mui/material';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import type { PautaRespostaDTO } from '../types';
import BadgeCorporativo from './BadgeCorporativo';
import { formatarDataHora } from '../utils/formatadores';

interface CartaoPautaProps {
  pauta: PautaRespostaDTO;
}



const CartaoPauta = ({ pauta }: CartaoPautaProps) => {
  return (
    <Card>
      <CardContent sx={{ p: 3 }}>
        <Typography
          variant="h6"
          component="h2"
          gutterBottom
          sx={{
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
        >
          {pauta.titulo}
        </Typography>

        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            mb: 2,
            minHeight: 40,
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden',
          }}
        >
          {pauta.descricao || 'Sem descrição informada.'}
        </Typography>

        <BadgeCorporativo
          tipo="neutro"
          icon={<CalendarTodayIcon fontSize="small" />}
          label={formatarDataHora(pauta.dataHoraCriacao)}
          variant="outlined"
        />
      </CardContent>
    </Card>
  );
};

export default CartaoPauta;
