package teoInfoTPE;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;
import javax.swing.JFormattedTextField;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JProgressBar;
import java.awt.Window.Type;

public class VentanaPrincipal extends JFrame implements ActionListener{

	private JButton btnHistograma;
	private JLabel lblSeleccione;
	private JButton btnMediaYDesvio;
	private JButton btnCodificar;
	private JButton btnDecodificar;
	private JButton btnRuido;
	private JButton btnPerdida;
	private JButton btnEntropias;
	private JButton btnMatrizCondicional;
	private JLabel lblIngreseHt;
	private JFormattedTextField IngresoHt;
	private JLabel lblEntropiasSinY;
	private JLabel lblhistogramasDeLos;
	private JLabel lblmatricesDeTrancin;
	private JLabel lblmediaYDesvio;
	private JLabel lblElCalculoDe;
	private JLabel lblNewLabel;
	private JLabel lblCanales;
	static JProgressBar progressBar;
	private JLabel lblProgreso;
	
	public VentanaPrincipal() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 654, 488);
		getContentPane().setLayout(null);
		
		this.setLocationRelativeTo(null);
		
		btnHistograma = new JButton("Histogramas");
		btnHistograma.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnHistograma.addActionListener(this);
		btnHistograma.setBounds(397, 70, 128, 25);
		getContentPane().add(btnHistograma);
		
		lblSeleccione = new JLabel("Presione el boton de lo que desee realizar (sobre una imagen dada)");
		lblSeleccione.setForeground(new Color(153, 50, 204));
		lblSeleccione.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 15));
		lblSeleccione.setBounds(63, 9, 520, 25);
		getContentPane().add(lblSeleccione);
		
		btnMediaYDesvio = new JButton("Media y Desvio");
		btnMediaYDesvio.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnMediaYDesvio.addActionListener(this);
		btnMediaYDesvio.setBounds(456, 152, 114, 25);
		getContentPane().add(btnMediaYDesvio);
		
		btnCodificar = new JButton("Codificar");
		btnCodificar.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnCodificar.addActionListener(this);
		btnCodificar.setBounds(180, 246, 114, 25);
		getContentPane().add(btnCodificar);
		
		btnDecodificar = new JButton("Decodificar");
		btnDecodificar.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnDecodificar.addActionListener(this);
		btnDecodificar.setBounds(397, 246, 114, 25);
		getContentPane().add(btnDecodificar);
		
		btnRuido = new JButton("Ruido");
		btnRuido.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnRuido.addActionListener(this);
		btnRuido.setBounds(123, 359, 114, 25);
		getContentPane().add(btnRuido);
		
		btnPerdida = new JButton("Perdida");
		btnPerdida.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnPerdida.addActionListener(this);
		btnPerdida.setBounds(411, 359, 114, 25);
		getContentPane().add(btnPerdida);
		
		btnEntropias = new JButton("Entropias");
		btnEntropias.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnEntropias.addActionListener(this);
		btnEntropias.setBounds(216, 35, 114, 25);
		getContentPane().add(btnEntropias);
		
		btnMatrizCondicional = new JButton("Matrices");
		btnMatrizCondicional.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		btnMatrizCondicional.addActionListener(this);
		btnMatrizCondicional.setBounds(386, 112, 107, 25);
		getContentPane().add(btnMatrizCondicional);
		
		lblIngreseHt = new JLabel("Ingrese Ht:");
		lblIngreseHt.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		lblIngreseHt.setBounds(63, 246, 61, 20);
		getContentPane().add(lblIngreseHt);
		
		try {
			IngresoHt = new JFormattedTextField(getMaskFormatter("#.####"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		IngresoHt.setBounds(123, 247, 48, 19);
		IngresoHt.setValue(new Double (0.00));
		getContentPane().add(IngresoHt);
		IngresoHt.setColumns(10);
		
		lblEntropiasSinY = new JLabel("-Entropias sin y con memoria:");
		lblEntropiasSinY.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		lblEntropiasSinY.setBounds(52, 39, 169, 15);
		getContentPane().add(lblEntropiasSinY);
		
		lblhistogramasDeLos = new JLabel("-Histogramas de los bloques con entropia menor, mayor y media:");
		lblhistogramasDeLos.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		lblhistogramasDeLos.setBounds(52, 71, 348, 20);
		getContentPane().add(lblhistogramasDeLos);
		
		lblmatricesDeTrancin = new JLabel("-Matrices de transicion entre los distintas intencidades de gris:");
		lblmatricesDeTrancin.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		lblmatricesDeTrancin.setBounds(53, 116, 334, 15);
		getContentPane().add(lblmatricesDeTrancin);
		
		lblmediaYDesvio = new JLabel("-Media y Desvio de los colores de los bloques con mayor y menor entropia:");
		lblmediaYDesvio.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 11));
		lblmediaYDesvio.setBounds(52, 157, 402, 15);
		getContentPane().add(lblmediaYDesvio);
		
		lblCanales = new JLabel("Canales");
		lblCanales.setForeground(new Color(60, 179, 113));
		lblCanales.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 15));
		lblCanales.setBounds(284, 310, 61, 14);
		getContentPane().add(lblCanales);
		
		lblElCalculoDe = new JLabel("El calculo se realiza mediante el ingreso de dos imagenes, siendo la primera la de salida y la segunda la de llegada");
		lblElCalculoDe.setFont(new Font("Microsoft YaHei UI", Font.ITALIC, 11));
		lblElCalculoDe.setBounds(34, 324, 594, 24);
		getContentPane().add(lblElCalculoDe);
		
		lblNewLabel = new JLabel("Codificacion");
		lblNewLabel.setForeground(new Color(255, 165, 0));
		lblNewLabel.setFont(new Font("Microsoft YaHei UI", Font.BOLD, 15));
		lblNewLabel.setBounds(273, 189, 101, 25);
		getContentPane().add(lblNewLabel);
		
		progressBar = new JProgressBar();
		progressBar.setForeground(new Color(0, 255, 0));
		progressBar.setBounds(193, 429, 252, 20);
		getContentPane().add(progressBar);
		
		lblProgreso = new JLabel("Progreso");
		lblProgreso.setForeground(new Color(0, 0, 128));
		lblProgreso.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
		lblProgreso.setBounds(193, 405, 68, 20);
		getContentPane().add(lblProgreso);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == btnCodificar) {
			Codificacion codificacion = new Codificacion();
			double ht = (double) IngresoHt.getValue();
			codificacion.codificarImagen(ht);
			JOptionPane.showMessageDialog(null, "Imagen Codificada");
		} else  if (e.getSource() == btnDecodificar) {
			Codificacion codificacion = new Codificacion();
			codificacion.decodificarImagen();
			JOptionPane.showMessageDialog(null, "Archivo Decodificado");
			} else if (e.getSource() == btnRuido) {
					progressBar.setValue(20);
					Canales canales = new Canales();
					canales.getRuido();
					JOptionPane.showMessageDialog(null, "Ruido calculado");
					} else if (e.getSource() == btnPerdida) {
						progressBar.setValue(20);
						Canales canales = new Canales();
						canales.getPerdida();
						JOptionPane.showMessageDialog(null, "Perdida calculada");
						} else if (e.getSource() == btnHistograma) {
							progressBar.setValue(20);
							Parte1 p1 = new Parte1();
							p1.getHistogramas();
							JOptionPane.showMessageDialog(null, "Histogramas generados");
							} else if (e.getSource() == btnMediaYDesvio){
								progressBar.setValue(20);
								Parte1 p1 = new Parte1();
								p1.calcularMediaYDesvioBloqueMayorYMenorH();
								JOptionPane.showMessageDialog(null, "Media y desvio calculados");
								} else if (e.getSource() == btnMatrizCondicional) {
									progressBar.setValue(20);
									Parte1 p1 = new Parte1();
									p1.getMatricesCondicionalesMayorYMenorH();
									JOptionPane.showMessageDialog(null, "Matrices almacenadas");
									 } else if (e.getSource() == btnEntropias) {
										 progressBar.setValue(20);
										 Parte1 p1 = new Parte1();
										 p1.calcularEntropias();
										 JOptionPane.showMessageDialog(null, "Entropias calculadass");
									 }
	}	
	
	private MaskFormatter getMaskFormatter(String format) throws java.text.ParseException {
	    MaskFormatter mask = null;
	    mask = new MaskFormatter(format);
		mask.setPlaceholderCharacter('0');
	    return mask;
	}
}


