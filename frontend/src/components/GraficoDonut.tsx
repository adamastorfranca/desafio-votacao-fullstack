import { useState, useCallback } from 'react';
import { Box, Typography } from '@mui/material';
import { PieChart, Pie, Cell, ResponsiveContainer } from 'recharts';

export interface DadoGrafico {
  nome: string;
  valor: number;
}

interface GraficoDonutProps {
  dados: DadoGrafico[];
  cores: Record<string, string>;
  alturaContainer?: number;
}

const GraficoDonut = ({ dados, cores, alturaContainer = 240 }: GraficoDonutProps) => {
  const [setorAtivo, setSetorAtivo] = useState<number | undefined>(undefined);

  const aoEntrarNoSetor = useCallback((_: unknown, indice: number) => {
    setSetorAtivo(indice);
  }, []);

  const total = dados.reduce((acc, d) => acc + d.valor, 0);
  if (total === 0) return null;

  const itemAtivo = setorAtivo !== undefined ? dados[setorAtivo] : undefined;
  const percentualAtivo = total > 0 && itemAtivo ? ((itemAtivo.valor / total) * 100).toFixed(1) : '0';

  return (
    <>
      <Box sx={{ position: 'relative', width: '100%', height: alturaContainer }}>
        <ResponsiveContainer width="100%" height={alturaContainer}>
          <PieChart>
            <Pie
              data={dados}
              cx="50%"
              cy="50%"
              innerRadius={60}
              outerRadius={90}
              paddingAngle={3}
              dataKey="valor"
              nameKey="nome"
              onMouseEnter={aoEntrarNoSetor}
              strokeWidth={2}
              stroke="#ffffff"
            >
              {dados.map((entrada, indice) => (
                <Cell
                  key={entrada.nome}
                  fill={cores[entrada.nome] ?? '#94a3b8'}
                  opacity={setorAtivo === undefined || indice === setorAtivo ? 1 : 0.6}
                  style={{ cursor: 'pointer', transition: 'opacity 0.2s' }}
                />
              ))}
            </Pie>
          </PieChart>
        </ResponsiveContainer>

        {}
        {itemAtivo && (
          <Box
            sx={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              textAlign: 'center',
              pointerEvents: 'none',
            }}
          >
            <Typography variant="h5" fontWeight={700} sx={{ color: '#334155', lineHeight: 1.2 }}>
              {itemAtivo.valor}
            </Typography>
            <Typography variant="caption" sx={{ color: '#64748b', fontSize: '0.7rem' }}>
              {percentualAtivo}%
            </Typography>
            <Typography variant="caption" display="block" sx={{ color: '#94a3b8', fontSize: '0.65rem' }}>
              {itemAtivo.nome}
            </Typography>
          </Box>
        )}
      </Box>

      {}
      <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2.5, mt: 1, flexWrap: 'wrap' }}>
        {dados.map((item, idx) => {
          const ativo = idx === setorAtivo;
          return (
            <Box
              key={item.nome}
              onMouseEnter={() => setSetorAtivo(idx)}
              onClick={() => setSetorAtivo(idx)}
              onMouseLeave={() => setSetorAtivo(undefined)}
              sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 0.75,
                cursor: 'pointer',
                opacity: setorAtivo === undefined || ativo ? 1 : 0.7,
                transition: 'opacity 0.2s',
                '&:hover': { opacity: 1 },
              }}
            >
              <span
                style={{
                  width: 10,
                  height: 10,
                  borderRadius: '50%',
                  backgroundColor: cores[item.nome] ?? '#94a3b8',
                  display: 'inline-block',
                }}
              />
              <Typography variant="caption" sx={{ color: ativo ? '#334155' : '#64748b', fontWeight: ativo ? 600 : 500 }}>
                {item.nome}
              </Typography>
            </Box>
          );
        })}
      </Box>
    </>
  );
};

export default GraficoDonut;
