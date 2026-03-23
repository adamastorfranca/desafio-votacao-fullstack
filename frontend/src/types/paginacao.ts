export interface PaginaSpringDTO<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface FiltroPautaDTO {
  statusSessao?: string;
  resultado?: string;
  periodo?: string;
}
