/**
 * Utilitários para formatação de data e hora garantindo o fuso horário America/Sao_Paulo
 */

export const converterParaData = (dataBase: string | Date): Date => {
  if (dataBase instanceof Date) return dataBase;
  let dataStr = dataBase;
  if (!dataStr.endsWith('Z') && !dataStr.match(/[+-]\d{2}:?\d{2}$/)) {
    dataStr += 'Z';
  }
  return new Date(dataStr);
};

export const formatarData = (dataBase: string | Date | null | undefined): string => {
  if (!dataBase) return '--';
  const data = converterParaData(dataBase);
  return new Intl.DateTimeFormat('pt-BR', {
    timeZone: 'America/Sao_Paulo',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(data);
};

export const formatarDataHora = (dataBase: string | Date | null | undefined): string => {
  if (!dataBase) return '--';
  const data = converterParaData(dataBase);
  return new Intl.DateTimeFormat('pt-BR', {
    timeZone: 'America/Sao_Paulo',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(data);
};

export const formatarDataSeparada = (dataBase: string | Date | null | undefined): { data: string; hora: string } => {
  if (!dataBase) return { data: '—', hora: '' };
  const data = converterParaData(dataBase);
  
  const formatadorData = new Intl.DateTimeFormat('pt-BR', {
    timeZone: 'America/Sao_Paulo',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
  
  const formatadorHora = new Intl.DateTimeFormat('pt-BR', {
    timeZone: 'America/Sao_Paulo',
    hour: '2-digit',
    minute: '2-digit'
  });
  
  return {
    data: formatadorData.format(data),
    hora: formatadorHora.format(data)
  };
};
