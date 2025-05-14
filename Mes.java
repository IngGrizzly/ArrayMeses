import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * Clase que representa una interfaz gráfica para mostrar un calendario interactivo de meses.
 * Permite visualizar y consultar los consumos eléctricos diarios por franjas horarias, así como realizar
 * una consulta general sobre el consumo mensual, mostrando el día de mayor y menor consumo.
 *
 * @author Juan Camilo Pardo
 * @author Sergio Felipe Rincon
 * @version 1.0
 * @since 13/05/2025
 */
public class Mes {

    /**
     * Mapa que almacena los consumos eléctricos por día, donde la clave es el mes y el día,
     * y el valor es un arreglo de 24 elementos que representan el consumo por hora.
     */
    private final Map<String, int[]> consumosPorDia = new HashMap<>();

    /**
     * Método principal que inicializa la interfaz gráfica con la ventana principal que muestra los meses.
     * 
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Mes().mCrearVentana());
    }

    /**
     * Crea la ventana principal que contiene los botones correspondientes a cada mes del año.
     * Al hacer clic en un botón de mes, se mostrarán los días correspondientes a ese mes.
     */
    private void mCrearVentana() {
        JFrame frame = new JFrame("Meses Interactivos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);

        String[] meses = {
                "Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 10));

        for (int i = 0; i < meses.length; i++) {
            final int mesIndex = i;
            JButton botonMes = new JButton(meses[i]);
            botonMes.addActionListener(e -> mMostrarDiasDelMes(meses[mesIndex], mesIndex));
            panel.add(botonMes);
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Muestra una ventana con los días del mes seleccionado, permitiendo la consulta del consumo eléctrico
     * por día y hora. También permite realizar una consulta general sobre el consumo de todo el mes.
     * 
     * @param nombreMes Nombre del mes seleccionado.
     * @param mesIndex Índice del mes en el array.
     */
    private void mMostrarDiasDelMes(String nombreMes, int mesIndex) {
        int dias = mObtenerCantidadDias(mesIndex);

        JFrame ventanaDias = new JFrame("Días de " + nombreMes);
        ventanaDias.setSize(600, 400);
        ventanaDias.setLocationRelativeTo(null);
        ventanaDias.setLayout(new BorderLayout());

        JPanel panelDias = new JPanel(new GridLayout(0, 7, 5, 5));

        for (int dia = 1; dia <= dias; dia++) {
            JButton botonDia = new JButton(String.valueOf(dia));
            int diaFinal = dia;
            botonDia.addActionListener(e -> {
                String clave = nombreMes + "-" + diaFinal;
                int[] consumos = mObtenerOGenerarConsumoParaDia(clave);

                int suma = Arrays.stream(consumos).sum();
                int franja1 = mSumaConsumoRango(consumos, 0, 6);
                int franja2 = mSumaConsumoRango(consumos, 7, 17);
                int franja3 = mSumaConsumoRango(consumos, 18, 23);

                StringBuilder detalleHoras = new StringBuilder();
                for (int h = 0; h < 24; h++) {
                    detalleHoras.append(String.format("Hora %02d: %d kWh\n", h, consumos[h]));
                }

                JOptionPane.showMessageDialog(null,
                        "Día " + diaFinal + " de " + nombreMes + "\n"
                                + "Franja 1 (00-06): " + franja1 + " kWh\n"
                                + "Franja 2 (07-17): " + franja2 + " kWh\n"
                                + "Franja 3 (18-23): " + franja3 + " kWh\n"
                                + "Consumo Total: " + suma + " kWh\n\n"
                                + "Consumo por Hora:\n" + detalleHoras,
                        "Consumo por Franjas y Horas",
                        JOptionPane.INFORMATION_MESSAGE);
            });
            panelDias.add(botonDia);
        }

        JButton consultaBtn = new JButton("Consulta General");
        consultaBtn.addActionListener(e -> mRealizarConsulta(nombreMes, dias));

        JPanel panelInferior = new JPanel();
        panelInferior.add(consultaBtn);

        ventanaDias.add(new JLabel("Selecciona un día:", SwingConstants.CENTER), BorderLayout.NORTH);
        ventanaDias.add(panelDias, BorderLayout.CENTER);
        ventanaDias.add(panelInferior, BorderLayout.SOUTH);
        ventanaDias.setVisible(true);
    }

    /**
     * Realiza una consulta general sobre el consumo del mes, mostrando el día con el mayor y menor consumo
     * y el valor total a pagar por el consumo mensual.
     * 
     * @param mes Nombre del mes a consultar.
     * @param dias Número de días del mes.
     */
    private void mRealizarConsulta(String mes, int dias) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int valorTotal = 0;
        int diaMin = -1;
        int diaMax = -1;

        for (int dia = 1; dia <= dias; dia++) {
            String clave = mes + "-" + dia;
            int[] consumos = mObtenerOGenerarConsumoParaDia(clave);
            int suma = Arrays.stream(consumos).sum();

            if (suma < min) {
                min = suma;
                diaMin = dia;
            }
            if (suma > max) {
                max = suma;
                diaMax = dia;
            }

            int franja1 = mSumaConsumoRango(consumos, 0, 6);
            int franja2 = mSumaConsumoRango(consumos, 7, 17);
            int franja3 = mSumaConsumoRango(consumos, 18, 23);

            valorTotal += (franja1 * 200) + (franja2 * 300) + (franja3 * 500);
        }

        JOptionPane.showMessageDialog(null,
                "Consulta para el mes de " + mes + ":\n"
                        + "Día de menor consumo: " + diaMin + " (" + min + " kWh)\n"
                        + "Día de mayor consumo: " + diaMax + " (" + max + " kWh)\n"
                        + "Valor total a pagar por el mes: " + valorTotal + " COP",
                "Consulta General",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Obtiene o genera el consumo eléctrico para un día específico. Si ya existen datos de consumo para ese día,
     * los retorna; de lo contrario, genera valores aleatorios para cada hora.
     * 
     * @param clave Clave que representa el mes y el día (ej. "Enero-1").
     * @return Un arreglo de 24 enteros representando el consumo eléctrico por hora.
     */
    private int[] mObtenerOGenerarConsumoParaDia(String clave) {
        if (consumosPorDia.containsKey(clave)) {
            return consumosPorDia.get(clave);
        }

        int[] consumos = new int[24];
        Random rand = new Random();

        for (int h = 0; h < 24; h++) {
            if (h >= 0 && h <= 6) {
                consumos[h] = rand.nextInt(201) + 100; // 100 a 300
            } else if (h >= 7 && h <= 17) {
                consumos[h] = rand.nextInt(301) + 300; // 300 a 600
            } else {
                consumos[h] = rand.nextInt(399) + 601; // 600 a 999
            }
        }

        consumosPorDia.put(clave, consumos);
        return consumos;
    }

    /**
     * Suma los consumos eléctricos dentro de un rango de horas.
     * 
     * @param consumos Arreglo que contiene los consumos por hora del día.
     * @param inicio Índice de la hora de inicio del rango.
     * @param fin Índice de la hora de fin del rango.
     * @return La suma de los consumos dentro del rango especificado.
     */
    private int mSumaConsumoRango(int[] consumos, int inicio, int fin) {
        int suma = 0;
        for (int i = inicio; i <= fin; i++) {
            suma += consumos[i];
        }
        return suma;
    }

    /**
     * Obtiene la cantidad de días de un mes, teniendo en cuenta si es un mes de 28, 30 o 31 días.
     * 
     * @param mesIndex Índice del mes (0 a 11).
     * @return El número de días en el mes.
     */
    private int mObtenerCantidadDias(int mesIndex) {
        return switch (mesIndex) {
            case 1 -> 28;
            case 3, 5, 8, 10 -> 30;
            default -> 31;
        };
    }
}
