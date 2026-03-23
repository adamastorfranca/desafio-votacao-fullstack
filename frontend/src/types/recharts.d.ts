import 'recharts';

declare module 'recharts' {
  interface PieProps {
    activeIndex?: number | number[];

    activeShape?: ((props: Record<string, unknown>) => React.ReactElement) | React.ReactElement;
  }
}
