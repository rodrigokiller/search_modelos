package search;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.html.HTMLEditorKit.Parser;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.crypto.dsig.keyinfo.KeyName;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.annotation.Documented;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;

public class Main extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JTextField tfPath;
	private JTextField tfFilter;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTable tableFiles;
	private JButton btnFiltros;
	private JButton btnFiltrar;
	private JButton btnOpen;
	private JRadioButton rdbtnBoleto;
	private JRadioButton rdbtnGerador;
	private JRadioButton rdbtnVetorh;
	private JLabel lblCaminho;
	private String path;
	private String strFiltro = "";
	private String aFiltro[];
	private boolean pathVal;
	private File myfile;
	private String caseS = "";
	private JCheckBox cbCaseS;
	private String tmp;		
	private JScrollPane spFiles;
	
	private String[] typefile;
	
	private TableRowSorter<TableModel> sorter; 

	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	  
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/ico/TrImpExp_MAINICON.ico")));
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("Modelo de relatórios Sapiens - Pesquisa");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/ico/TrImpExp_MAINICON.ico")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 826, 587);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//btnOpen.setAction(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));		
		setContentPane(contentPane);
		
		tfPath = new JTextField();			
		tfPath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				/*if (e.getKeyCode() == InputEvent.SHIFT_MASK)
				{
					if (e.getKeyCode() == (KeyEvent.VK_8, ));
						System.out.println("Teste");
				}*/
				if (e.getKeyCode() == KeyEvent.VK_F3)
					file_open();
			}
		});
		tfPath.addActionListener(this);			
		tfPath.setColumns(10);
		
		lblCaminho = new JLabel("Caminho");
		
		btnOpen = new JButton(".");		
		btnOpen.addActionListener(this);
		
		btnFiltrar = new JButton(".");
		btnFiltrar.setVisible(false);
		btnFiltrar.addActionListener(this);		
		
		tfFilter = new JTextField();
		tfFilter.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {								
				strFiltro = tfFilter.getText();
				
				// Retira excesso de asteríscos (*)
				while (strFiltro.contains("**"))				
					strFiltro = strFiltro.substring(0, strFiltro.indexOf("**")) + strFiltro.substring(strFiltro.indexOf("**")+1, strFiltro.length());									
				if (strFiltro.startsWith("*"))
					strFiltro = strFiltro.substring(1, strFiltro.length());
				//file_filtrar();
				aFiltro = verifCuringa(strFiltro);				
				file_filtraTexto();
			}
		});
		tfFilter.setColumns(10);
		
		JLabel lblFiltrar = new JLabel("Pesquisar");
		
		rdbtnGerador = new JRadioButton("Gerador");
		rdbtnGerador.addActionListener(this);
		rdbtnGerador.setSelected(true);
		buttonGroup.add(rdbtnGerador);
		
		rdbtnBoleto = new JRadioButton("Boleto");
		rdbtnBoleto.addActionListener(this);
		buttonGroup.add(rdbtnBoleto);
		
		btnFiltros = new JButton("Copiar");
		btnFiltros.setVisible(false);
		btnFiltros.setToolTipText("Copiar para a área de transferência");
		btnFiltros.addActionListener(this);
		
		
		spFiles = new JScrollPane();
		
		cbCaseS = new JCheckBox("Forçar maiúsculas/minúsculas");
		cbCaseS.addActionListener(this);
		
		rdbtnVetorh = new JRadioButton("Vetorh");
		rdbtnVetorh.addActionListener(this);
		buttonGroup.add(rdbtnVetorh);
		
		JLabel lblRodrigoSanguanini = new JLabel("Rodrigo Sanguanini - rodrigo@seniordocontestado.com.br    ");
		
		JLabel lblValpha = new JLabel("v0.1 (Alpha)");
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(spFiles, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(rdbtnGerador)
							.addGap(18)
							.addComponent(rdbtnBoleto)
							.addGap(18)
							.addComponent(rdbtnVetorh, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(cbCaseS, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 288, Short.MAX_VALUE)
							.addComponent(btnFiltros, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(tfFilter, GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFiltrar, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblCaminho, GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE)
								.addComponent(tfPath, GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOpen, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblFiltrar)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblRodrigoSanguanini)
							.addPreferredGap(ComponentPlacement.RELATED, 420, Short.MAX_VALUE)
							.addComponent(lblValpha, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(10)
					.addComponent(lblCaminho)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnOpen))
					.addGap(18)
					.addComponent(lblFiltrar)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnFiltrar))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(rdbtnGerador)
						.addComponent(rdbtnBoleto)
						.addComponent(btnFiltros)
						.addComponent(cbCaseS)
						.addComponent(rdbtnVetorh))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(spFiles, GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
					.addGap(9)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblRodrigoSanguanini)
						.addComponent(lblValpha)))
		);
		
		tableFiles = new JTable();
		tableFiles.setAutoCreateRowSorter(true);
		tableFiles.setModel (new DefaultTableModel(
			new Object[][] {							
			},
			new String[] {
				"Arquivo", "Descrição", "Data modificação"
			}
		){
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		}
		);			
		tableFiles.addKeyListener(new KeyAdapter() {
	        boolean ctrlPressed = false;
	        boolean cPressed = false;

	        @Override
	        public void keyPressed(KeyEvent e) {
	            switch(e.getKeyCode()) {
	            case KeyEvent.VK_C:
	                cPressed=true;

	                break;
	            case KeyEvent.VK_CONTROL:
	                ctrlPressed=true;
	                break;
	            }

	            if(ctrlPressed && cPressed) {
	                //System.out.println("Blocked CTRl+C");
	            	doCopy();
	                e.consume();// Stop the event from propagating.
	            }
	        }

	        @Override
	        public void keyReleased(KeyEvent e) {
	            switch(e.getKeyCode()) {
	            case KeyEvent.VK_C:
	                cPressed=false;

	                break;
	            case KeyEvent.VK_CONTROL:
	                ctrlPressed=false;
	                break;
	            }

	            if(ctrlPressed && cPressed) {
	            	doCopy();
	                e.consume();// Stop the event from propagating.
	            }
	        }
	    });
		
		
		spFiles.setViewportView(tableFiles);
		contentPane.setLayout(gl_contentPane);
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == btnFiltros)
			//filtros();
			doCopy();
		if (e.getSource() == btnOpen)
			file_open();
		if (e.getSource() == btnFiltrar)
			file_filtrar();
		if (e.getSource() == tfPath){
			path = tfPath.getText();		
			if (!(path == null || path.equalsIgnoreCase("")))
				myfile = new File(path);
			file_filtrar();		
		}
		if ((e.getSource() == rdbtnGerador && pathVal))
			file_filtrar();
		if ((e.getSource() == rdbtnBoleto && pathVal))
			file_filtrar();
		if ((e.getSource() == rdbtnVetorh && pathVal))
			file_filtrar();
		if (e.getSource() == tfFilter)
			file_filtrar();
		if (e.getSource() == cbCaseS)			
			file_filtraTexto();		
	}
	
	public void doCopy(){
		int col = 0;
		String data = "";
		final List<File> fs = new ArrayList<File>();
		File f;
	    int rows [] = tableFiles.getSelectedRows();
	    for (int i = 0; i < rows.length; i++) {
	    	int row = rows[i];
		    if (col != -1 && row != -1) {
		        Object value = tableFiles.getValueAt(row, col);	    			        
		        if (value == null) {
		            data = "";		           
		        } else {	        	
		            data = value.toString();
		            f = new File(path + "\\" + data);	
		            fs.add(f);
		        }

		        final StringSelection selection = new StringSelection(path + "\\" + data);		        

		        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();		        
		        //clipboard.setContents(selection, selection);
		        
		        clipboard.setContents(
		        	    new Transferable() {
		        	        @Override
		        	        public DataFlavor[] getTransferDataFlavors() {
		        	            return new DataFlavor[] { DataFlavor.javaFileListFlavor };
		        	        }

		        	        @Override
		        	        public boolean isDataFlavorSupported(DataFlavor flavor) {
		        	            return DataFlavor.javaFileListFlavor.equals(flavor);
		        	        }

		        	        @Override
		        	        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		        	            return fs;
		        	        }
		        	    }, null
		        	);
		        //fs.clear();
		    }		   
		}

	}
	
	public void filtros(){
		/*Filtros f = new Filtros();
		f.setVisible(true);*/
	}	
	
	public  static Collection<File> listFiles(File directory,FilenameFilter filter,boolean recurse){
	    Vector<File> files = new Vector<File>();
	    File[] entries = directory.listFiles();
	    if(entries!=null){
	        for (File entry : entries){
	            if (filter == null || filter.accept(directory, entry.getName())){
	                    files.add(entry);
	            }

	            if (recurse && entry.isDirectory()){
	                    files.addAll(listFiles(entry, filter, recurse));
	            }
	        }
	    }
	    return files;
	}
	
    public void file_open(){
        JFileChooser fc = new JFileChooser("C:\\Senior\\Sapiens\\Modelos");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = fc.showOpenDialog(this);        
        if(r == fc.CANCEL_OPTION)
            return;        
        myfile = fc.getSelectedFile();            
        if(myfile == null || myfile.getName().equals(""))
        {
            JOptionPane.showMessageDialog(this, "Selecione um arquivo!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {     
        	contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            path = myfile.getAbsolutePath();
            lblCaminho.setText("Caminho - " + myfile.getAbsolutePath());
            myfile = new File(path);            
            pathVal = true;
            insere_tabela(myfile);
            contentPane.setCursor(Cursor.getDefaultCursor());
        }
        catch(Exception e){
        	lblCaminho.setText("Caminho");
        	pathVal = false;
        	JOptionPane.showMessageDialog(null, "Erro: " + e);
        }
    }
	
	public void file_filtrar(){		
		try
		{         									
			path = tfPath.getText();
			if (myfile == null)
				myfile = new File(path);
			if (path == null || path.equalsIgnoreCase("")){				
				if (myfile.isDirectory()){					
					pathVal = true;
					insere_tabela(myfile);
				}
			}
			else{												
				if (!myfile.isDirectory()){
					JOptionPane.showMessageDialog(null, "Insira um caminho válido");
					limpa_tabela();
					lblCaminho.setText("Caminho");
					pathVal = false;
				}
				else{					
					pathVal = true;
					lblCaminho.setText("Caminho - " + myfile.getAbsolutePath());
					insere_tabela(myfile);
				}
			}			
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Erro: " + e);
		}
	}
	
	public void limpa_tabela(){		
		tmp = tfFilter.getText();
		tfFilter.setText("");
		strFiltro = "";
		file_filtraTexto();		
		int rowCount = tableFiles.getRowCount();
		javax.swing.table.DefaultTableModel dtm = (javax.swing.table.DefaultTableModel)tableFiles.getModel();
		if (rowCount > 0)
		{					
			for (int i = 0; i < rowCount; i++) {
				dtm.removeRow(0);												
			}										
		}
		//strFiltro = tfFilter.getText();
		//file_filtraTexto();
	}
	
	public void insere_tabela(File myfile){		
		if (rdbtnGerador.isSelected()){
			limpa_tabela();
			String typefile[] = new String[1];
			typefile[0] = ".GER";			
			insere_tabela_dados(typefile);
		}			
		else if (rdbtnBoleto.isSelected()){
			limpa_tabela();
			String typefile[] = new String[1];
			typefile[0] = ".BLO";						
			insere_tabela_dados(typefile);
		}
		else if (rdbtnVetorh.isSelected()){
			limpa_tabela();			
			String typefile[] = new String[40];
			typefile[0] = ".APU";
			typefile[1] = ".ASO";
			typefile[2] = ".ASS";
			typefile[3] = ".AVA";
			typefile[4] = ".CAD";
			typefile[5] = ".CAG";
			typefile[6] = ".CAR";
			typefile[7] = ".CEN";
			typefile[8] = ".CHE";
			typefile[9] = ".COL";
			typefile[10] = ".CRE";
			typefile[11] = ".CTB";
			typefile[12] = ".EMP";
			typefile[13] = ".ENV";
			typefile[14] = ".EPT";
			typefile[15] = ".FGT";
			typefile[16] = ".GER";
			typefile[17] = ".GRC";
			typefile[18] = ".GRL";
			typefile[19] = ".HAB";
			typefile[20] = ".ICT";
			typefile[21] = ".INT";
			typefile[22] = ".IRF";
			typefile[23] = ".JUR";
			typefile[24] = ".LAN";
			typefile[25] = ".LIE";
			typefile[26] = ".OPE";
			typefile[27] = ".ORC";
			typefile[28] = ".PAR";
			typefile[29] = ".PES";
			typefile[30] = ".PRE";
			typefile[31] = ".PRO";
			typefile[32] = ".PRV";
			typefile[33] = ".QUA";
			typefile[34] = ".RCS";
			typefile[35] = ".SAL";
			typefile[36] = ".SIN";			
			typefile[37] = ".VAL";
			typefile[38] = ".CTP";
			typefile[39] = ".TAR";
						
			insere_tabela_dados(typefile);			
		}		
	}
	
	public void insere_tabela_dados(String typefile[]){
		Collection foundFiles = listFiles(myfile, null, true); // chama a função collection que não fui eu que criei, mas pega todos os arquivos de uma pasta
		Object fF [] = foundFiles.toArray(); // cria um objeto vetor com os arquivos que retornaram acima
		File fileList[] = new File[fF.length];  // cria um vetor de File, do tamanho do vetor de objetos

		String desc = "";
		String data = "";
		for (int i = 0; i < fF.length; i++) {            	
			fileList[i] = new File(fF[i].toString()); // preenche o vetor de arquivos com todos os arquivos
			try{
				if (!fileList[i].isDirectory()){
					FileReader fr = new FileReader(fileList[i]);
					BufferedReader br = new BufferedReader(fr);							
					RandomAccessFile raf = new RandomAccessFile(fileList[i], "r");				
					raf.seek(0xA);	
					int leng;
					if (typefile.equals(".ger"))
						leng = 49;
					else if (typefile.equals(".blo"))
						leng = 70;
					else
						//leng = 1024;
						leng = 49;
					byte bt[] = new byte[leng];				
					raf.read(bt, 0, leng);
					desc = "";
					for (int j = 0; j < bt.length; j++) {					
						String hex = Integer.toHexString(bt[j]);		
						if (hex.length() == 1)
							hex = "0" + hex;
						if (hex.length() > 2)
							hex = hex.substring(hex.length()-2, hex.length());
						//System.out.println(j + " " + hex);
						if (!hex.equals("00"))
							desc = desc + hexToASCII(hex);					
					}
					br.close();		
					raf.close();

					DateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
					data = formatData.format(new Date(fileList[i].lastModified()));  
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(null, "Erro: " + e);
			}

			// Aqui é a inserção dos arquivos na tabela
			for (int j = 0; j < typefile.length; j++) {
				if ((fileList[i].getName().toUpperCase().endsWith(typefile[j]) || typefile == null) && (desc.contains(strFiltro))){
					javax.swing.table.DefaultTableModel dtm =
							(javax.swing.table.DefaultTableModel)
							tableFiles.getModel();
					dtm.addRow(new Object[]{fileList[i].getName(),
							desc, data});
				}
			}
			/*if ((fileList[i].getName().toUpperCase().endsWith(typefile) || typefile == null) && (desc.contains(strFiltro))){
					javax.swing.table.DefaultTableModel dtm =
							(javax.swing.table.DefaultTableModel)
							tableFiles.getModel();
					dtm.addRow(new Object[]{fileList[i].getName(),
							desc, data});
				}*/			
		}
		tfPath.setText(path);
		if (!tmp.isEmpty()){
			tfFilter.setText(tmp);
			strFiltro = tfFilter.getText();
		}
		file_filtraTexto();
	}

	public static String hexToASCII(String hex){        
		if(hex.length()%2 != 0){
			System.err.println("requires EVEN number of chars");
			return null;
		}
		StringBuilder sb = new StringBuilder();                
		//Convert Hex 0232343536AB into two characters stream.
		for( int i=0; i < hex.length()-1; i+=2 ){
			/*
			 * Grab the hex in pairs
			 */
			String output = hex.substring(i, (i + 2));
			/*
			 * Convert Hex to Decimal
			 */
			int decimal = Integer.parseInt(output, 16);                  
			sb.append((char)decimal);              
		}            
		return sb.toString();
	} 
	
	public void file_filtraTexto(){
		if (cbCaseS.isSelected())
			caseS = "";
		else
			caseS = "(?i)";
		List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>(2);
		
		// Aqui ele vai adicionar para cada filtro um novo filtro. Mas não é nesse momento que será fieto o filtro
		if (aFiltro != null){		
			for (int i = 0; i < aFiltro.length; i++) {
				filters.add(RowFilter.regexFilter(caseS + aFiltro[i], 1));				
			}		
		}
		else
			filters.add(RowFilter.regexFilter(caseS + strFiltro, 1));
		//filters.add(RowFilter.regexFilter(caseS + "balancete", 1));
		//filters.add(RowFilter.regexFilter(caseS + "meses", 1));
		
		TableModel tablemodel = tableFiles.getModel();
		sorter = new TableRowSorter<TableModel>(tablemodel);			
		try {
			sorter.setRowFilter(RowFilter.andFilter(filters));			
		} catch (PatternSyntaxException pse) {
			JOptionPane.showMessageDialog(null, "Erro: " + pse);					
		}	
		tableFiles.setRowSorter(sorter);
	}
	
	public String[] verifCuringa(String strFilt){		
		if (strFilt.length() > 1 && strFilt.contains("*")){
			int j = 0;
			for (int i = 0; i < strFilt.length(); i++) {
				if (strFilt.substring(i, i+1).equals("*"))
					j++;				
			}		
			int f = 0;
			if (!strFilt.endsWith("*"))
				f++;
			String[] lisStr = new String[j+f];
			String strtmp = strFilt;
			if (j > 0)
				for (int i = 0; i < j; i++) {
					lisStr[i] = strtmp.substring(0, strtmp.indexOf("*"));
					strtmp = strtmp.substring(strtmp.indexOf("*")+1, strtmp.length());				
				}
			if (!strFilt.endsWith("*"))
				lisStr[j] = strtmp;
			return lisStr;
		}
		else
			return null;
	}

	Comparator<String> comparator = new Comparator<String>() {
		public int compare(String s1, String s2) {
			String[] strings1 = s1.split("\\s");
			String[] strings2 = s2.split("\\s");
			return strings1[strings1.length - 1]
					.compareTo(strings2[strings2.length - 1]);
		}
	};
}
