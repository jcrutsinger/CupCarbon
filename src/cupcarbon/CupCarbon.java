/*----------------------------------------------------------------------------------------------------------------
 * CupCarbon: A Smart City & IoT Wireless Sensor Network Simulator
 * www.cupcarbon.com
 * ----------------------------------------------------------------------------------------------------------------
 * Copyright (C) 2013-2016 CupCarbon
 * ----------------------------------------------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *----------------------------------------------------------------------------------------------------------------
 * CupCarbon U-One is part of the research project PERSEPTEUR supported by the 
 * French Agence Nationale de la Recherche ANR 
 * under the reference ANR-14-CE24-0017-01. 
 * ----------------------------------------------------------------------------------------------------------------
 **/

package cupcarbon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.mapviewer.GeoPosition;

import actions_ui.Historic;
import arduino.Arduino;
import device.Device;
import device.DeviceList;
import geo_objects.BuildingList;
import map.MapLayer;
import map.RandomDevices;
import map.WorldMap;
import markers.Marker;
import markers.MarkerList;
import osm.City;
import perso.ExampleClass;
import perso.MonAlgoClass;
import perso.MyClass;
import project.Project;
import simulation.FaultInjector;
import solver.CharlySchedul;
import solver.EnvelopeJarvis;
import solver.EnvelopeLPCN;
import solver.EnvelopeLPCNMobile;
import solver.NetworkCenter;
import solver.NetworkEnvelopeC;
import solver.NetworkEnvelopeP;
import solver.NetworkPerso;
import solver.SensorSetCover;
import solver.SensorTargetCoverageRun;
import solver.SolverProxyParams;

/**
 * @author Ahcene Bounceur
 * @author Lounis Massinissa
 * @version 2.9.1 (U-One)
 */

public class CupCarbon {
	
	public static final int TWO_D = 0;
	public static final int THREE_D_UNITY = 1;
	
	public static int ihmType = THREE_D_UNITY;
	

	private JFrame mainFrame;
	public static JLabel lblSimulation = new JLabel(" | Simulation");
	private AboutCupCarbon aboutBox = null;
	private HelpCupCarbon helpWindow = new HelpCupCarbon();
	private JDesktopPane desktopPane = new JDesktopPane();	
	public static JLabel label;
	public static JLabel sspeedLabel;
	private JCheckBoxMenuItem chckbxmntmAddSelection;
	private ScriptWindow comScriptWindow = new ScriptWindow();
	private SolverProxyParamsWindow solverProxyParamsWindow = new SolverProxyParamsWindow();
	private CupCarbonMap cupCarbonMap;
	private GpsWindow gpsWindow = new GpsWindow();
	private DeviceParametersWindow deviceParametersWindow = new DeviceParametersWindow();
	private RadioParametersWindow radioParametersWindow = new RadioParametersWindow();
	//private FlyingObjParametersWindow flyingObjParametersWindow = new FlyingObjParametersWindow();
	private InformationWindow infoWindow = new InformationWindow();
	private WisenSimulationWindow wsnSimWindow = new WisenSimulationWindow();
	private VisualisationPorts visualisationWindow = new VisualisationPorts();

	private JPanel panel_1;
	private JLabel lblNodesNumber;
	
	public static int simulationNumber = 0;
	private JTextField cityTextEdit;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	protected FaultInjector faultInjector = null;

	private JTextField textField;
	private JTextField textField_1;
	
	public static JLabel labelMag;
	
	private EnvelopeLPCN lpcn = null ;
	private EnvelopeLPCNMobile lpcnm = null;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {					
				try {					
					FileInputStream licenceFile = new FileInputStream("cupcarbon_licence.txt");
					int c;
					while ((c = licenceFile.read()) != -1) {
						System.out.print((char) c);
					}
					System.out.println();
					licenceFile.close();
					CupCarbon window = new CupCarbon();
					window.mainFrame.setVisible(true);
					setProxy();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}	
	
	/**
	 * Create the application.
	 */
	public CupCarbon() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.getContentPane().setFont(new Font("Arial", Font.PLAIN, 12));
		mainFrame.setFont(new Font("Arial", Font.PLAIN, 12));
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/cupcarbon_logo_small.png"));
		mainFrame.setTitle("CupCarbon "+Version.VERSION);
		mainFrame.setBounds(100, 0, 1000, 700);
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Arial", Font.PLAIN, 12));
		mainFrame.setJMenuBar(menuBar);

		JMenu mnProject = new JMenu("Project");
		mnProject.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "projects_folder_badged.png"));
		menuBar.add(mnProject);
		mainFrame.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				int n = JOptionPane.showConfirmDialog(mainFrame,
						"Would you like to quit ?", "Quit",
						JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					//Project.saveProject();
					System.exit(0);
				}
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
		});

		JMenuItem mntmNewProject = new JMenuItem("New Project");
		mntmNewProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
		mntmNewProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileFilter projectFilter = new FileFilter() {
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						else if (f.getName().endsWith(".cup"))
							return true;
						else
							return false;
					}

					public String getDescription() {
						return "CupCarbon Project";
					}
				};

				JFileChooser fc = new JFileChooser("New CupCarbon Project");
				fc.setFileFilter(projectFilter);
				int val = fc.showDialog(fc, "New Project");
				if (val == 0) {
					Project.newProject(fc.getSelectedFile().getParent() + File.separator + fc.getSelectedFile().getName(), fc.getSelectedFile().getName());
				}
				cupCarbonMap.setTitle("CupCarbon Map : "+fc.getSelectedFile().getName());
			}
		});
		mntmNewProject.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "folder_new.png"));
		mnProject.add(mntmNewProject);
		
		JMenuItem mntmNewProjectFc = new JMenuItem("New Project From the Current");
		mntmNewProjectFc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.SHIFT_MASK));
		mntmNewProjectFc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileFilter projectFilter = new FileFilter() {
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						else if (f.getName().endsWith(".cup"))
							return true;
						else
							return false;
					}

					public String getDescription() {
						return "CupCarbon Project";
					}
				};

				JFileChooser fc = new JFileChooser("New CupCarbon Project");
				fc.setFileFilter(projectFilter);
				int val = fc.showDialog(fc, "New Project");
				if (val == 0) {
					Project.newProjectFC(fc.getSelectedFile().getParent() + File.separator + fc.getSelectedFile().getName(), fc.getSelectedFile().getName());
				}
				cupCarbonMap.setTitle("CupCarbon Map : "+fc.getSelectedFile().getName());
			}
		});
		mntmNewProjectFc.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "folder_new.png"));
		mnProject.add(mntmNewProjectFc);

		JMenuItem mntmOpenProject = new JMenuItem("Open Project");
		mntmOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
		mntmOpenProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileFilter projectFilter = new FileFilter() {
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						else if (f.getName().endsWith(".cup"))
							return true;
						else
							return false;
					}

					public String getDescription() {
						return "CupCarbon files";
					}
				};

				JFileChooser fc = new JFileChooser("Open CupCarbon Project");
				fc.setFileFilter(projectFilter);
				int val = fc.showDialog(fc, "Open Project");
				if (val == 0) {
					Project.openProject(fc.getSelectedFile().getParent(), fc.getSelectedFile().getName());
					cupCarbonMap.setTitle("CupCarbon Map : "+fc.getSelectedFile().getName());
				}				
			}
		});
		mntmOpenProject.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "folder.png"));
		mnProject.add(mntmOpenProject);

		JMenuItem mntmSaveProject = new JMenuItem("Save Project");
		mntmSaveProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		mntmSaveProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Project.saveProject();
			}
		});
		
		JMenuItem mntmOpenTheLast = new JMenuItem("Open the Last Project");
		mntmOpenTheLast.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "folder.png"));
		mntmOpenTheLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = Project.openRecentProject();
				cupCarbonMap.setTitle("CupCarbon Map : "+name);
			}
		});
		mnProject.add(mntmOpenTheLast);
		mntmSaveProject.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "Enregistrer.png"));
		mnProject.add(mntmSaveProject);
		
		JSeparator separator_20 = new JSeparator();
		mnProject.add(separator_20);
		
		JMenuItem mntmReset = new JMenuItem("Reset");
		mntmReset.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "reset.png"));
		mntmReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(mainFrame, "Would you like to reset?", "Reset", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					cupCarbonMap.setTitle("CupCarbon Map");
					Project.reset();
				}
			}
		});
		mnProject.add(mntmReset);

		JSeparator separator = new JSeparator();
		mnProject.add(separator);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "gnome_logout.png"));
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int n = JOptionPane.showConfirmDialog(mainFrame, "Would you like to quit?", "Quit", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					System.exit(0);
				}
			}
		});
		mnProject.add(mntmQuit);

		JMenu mnEdition = new JMenu("Edition");
		mnEdition
				.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "blockdevice.png"));
		menuBar.add(mnEdition);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Undo");
		mntmNewMenuItem_2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Historic.undo();
			}
		});
		mntmNewMenuItem_2.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "arrow_rotate_clockwise.png"));
		mnEdition.add(mntmNewMenuItem_2);

		JMenuItem mntmRetry = new JMenuItem("Redo");
		mntmRetry.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
		mntmRetry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Historic.redo();
			}
		});
		mntmRetry.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "arrow_rotate_anticlockwise.png"));
		mnEdition.add(mntmRetry);

		JSeparator separator_2 = new JSeparator();
		mnEdition.add(separator_2);

		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "cut.png"));
		mnEdition.add(mntmCut);

		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "copy.png"));
		mnEdition.add(mntmCopy);

		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "paste.png"));
		mnEdition.add(mntmPaste);

		JMenuItem mntmDelete = new JMenuItem("Delete");
		mntmDelete.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "Supprimer.png"));
		mnEdition.add(mntmDelete);

		JSeparator separator_3 = new JSeparator();
		mnEdition.add(separator_3);

		JMenuItem mntmMove = new JMenuItem("Move");
		mntmMove.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "move2red.png"));
		mnEdition.add(mntmMove);

		JMenu mnSelection = new JMenu("Selection");
		mnSelection.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "shape_square_select.png"));
		menuBar.add(mnSelection);

		JMenuItem mntmSelectAll = new JMenuItem("Select All");
		mntmSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(true, -1, true);
				WorldMap.setSelectionOfAllMarkers(true, -1, true);
			}
		});
		mntmSelectAll.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "shapes_many_select.png"));
		mnSelection.add(mntmSelectAll);

		JMenuItem mntmDeselectAll = new JMenuItem("Deselect All");
		mntmDeselectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, -1, true);
				WorldMap.setSelectionOfAllMarkers(false, -1, true);
			}
		});
		mntmDeselectAll.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "selection.png"));
		mnSelection.add(mntmDeselectAll);

		JMenuItem mntmInvertSelection = new JMenuItem("Invert Selection");
		mntmInvertSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.invertSelection();
			}
		});
		mntmInvertSelection.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "stock_filters_invert.png"));
		mnSelection.add(mntmInvertSelection);
		
		JSeparator separator_19 = new JSeparator();
		mnSelection.add(separator_19);
		
		JMenu mnSelectById = new JMenu("Select by Id");
		mnSelection.add(mnSelectById);
		
		JMenu mnSelectByMy = new JMenu("Select by My");
		mnSelection.add(mnSelectByMy);
		
		textField_1 = new JTextField();
		textField_1.setText("0");
		mnSelectByMy.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DeviceList.selectByMy(textField_1.getText());
			}
		});
		mnSelectByMy.add(btnSelect);
		
		textField = new JTextField();
		textField.setText("1");
		mnSelectById.add(textField);
		textField.setColumns(10);
		
		JButton btnOk_1 = new JButton("Select");
		btnOk_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DeviceList.selectById(textField.getText());
			}
		});
		mnSelectById.add(btnOk_1);

		JSeparator separator_1 = new JSeparator();
		mnSelection.add(separator_1);
		
		JMenuItem mntmSelectSensorsWithout = new JMenuItem("Select Sensors Without Script");
		mntmSelectSensorsWithout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DeviceList.selectWitoutScript();
			}
		});
		mnSelection.add(mntmSelectSensorsWithout);
		
		JMenuItem mntmSelectSensorsWithout_1 = new JMenuItem("Select Sensors Without GPS");
		mntmSelectSensorsWithout_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeviceList.selectWitoutGps();
			}
		});
		mnSelection.add(mntmSelectSensorsWithout_1);
		
		
		JMenuItem mntmSelectMarkedSensors = new JMenuItem("Select Marked Sensors");
		mntmSelectMarkedSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DeviceList.selectMarkedSensors();
			}
		});
		mnSelection.add(mntmSelectMarkedSensors);
		
		JSeparator separator_14 = new JSeparator();
		mnSelection.add(separator_14);

		chckbxmntmAddSelection = new JCheckBoxMenuItem("Add Selection");
		chckbxmntmAddSelection.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "layer_select.png"));
		mnSelection.add(chckbxmntmAddSelection);

		JSeparator separator_10 = new JSeparator();
		mnSelection.add(separator_10);

		JMenu mnSelectAll = new JMenu("Select All ...");
		mnSelection.add(mnSelectAll);

		JMenuItem mntmSelectAllSensors = new JMenuItem("Select All Sensors");
		mnSelectAll.add(mntmSelectAllSensors);
		mntmSelectAllSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(true, Device.SENSOR,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllSensors.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenuItem mntmSelectAllGasses = new JMenuItem("Select All Gasses");
		mnSelectAll.add(mntmSelectAllGasses);
		mntmSelectAllGasses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(true, Device.GAS,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllGasses.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenuItem mntmSelectAllMobiles = new JMenuItem("Select All Mobiles");
		mnSelectAll.add(mntmSelectAllMobiles);
		mntmSelectAllMobiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WorldMap.setSelectionOfAllNodes(true, Device.MOBILE,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllMobiles.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenuItem mntmSelectAllMobiles_1 = new JMenuItem(
				"Select All Mobiles WR");
		mnSelectAll.add(mntmSelectAllMobiles_1);
		mntmSelectAllMobiles_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WorldMap.setSelectionOfAllNodes(true, Device.MOBILE_WR,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllMobiles_1.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenuItem mntmSelectAllRouters = new JMenuItem("Select All Media Sensors");
		mnSelectAll.add(mntmSelectAllRouters);
		mntmSelectAllRouters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(true, Device.MEDIA_SENSOR,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllRouters.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenuItem mntmSelectAllFlyingObjects = new JMenuItem(
				"Select All Flying Objects");
		mnSelectAll.add(mntmSelectAllFlyingObjects);
		mntmSelectAllFlyingObjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(true, Device.FLYING_OBJECT,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllFlyingObjects.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenuItem mntmSelectAllBase = new JMenuItem("Select All Base Stations");
		mnSelectAll.add(mntmSelectAllBase);
		mntmSelectAllBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(true, Device.BASE_STATION,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllBase.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenuItem mntmSelectAllMarkers = new JMenuItem("Select All Markers");
		mnSelectAll.add(mntmSelectAllMarkers);
		mntmSelectAllMarkers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllMarkers(true, Device.MARKER,
						chckbxmntmAddSelection.getState());
			}
		});
		mntmSelectAllMarkers.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "select_node.png"));

		JMenu mnDeselectAll = new JMenu("Deselect All ...");
		mnSelection.add(mnDeselectAll);

		JMenuItem mntmDeselectAllSensors = new JMenuItem("Deselect All Sensors");
		mnDeselectAll.add(mntmDeselectAllSensors);
		mntmDeselectAllSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, Device.SENSOR, true);
			}
		});
		mntmDeselectAllSensors.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JMenuItem mntmDeselectAllGasses = new JMenuItem("Deselect All Gasses");
		mnDeselectAll.add(mntmDeselectAllGasses);
		mntmDeselectAllGasses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, Device.GAS, true);
			}
		});
		mntmDeselectAllGasses.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JMenuItem mntmDeselectAllMobiles = new JMenuItem("Deselect All Mobiles");
		mnDeselectAll.add(mntmDeselectAllMobiles);
		mntmDeselectAllMobiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, Device.MOBILE, true);
			}
		});
		mntmDeselectAllMobiles.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JMenuItem mntmDeselectAllMobiles_1 = new JMenuItem(
				"Deselect All Mobiles WR");
		mnDeselectAll.add(mntmDeselectAllMobiles_1);
		mntmDeselectAllMobiles_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, Device.MOBILE_WR, true);
			}
		});
		mntmDeselectAllMobiles_1.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JMenuItem mntmDeselectAllRouters = new JMenuItem("Deselect All Media Sensors");
		mnDeselectAll.add(mntmDeselectAllRouters);
		mntmDeselectAllRouters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, Device.MEDIA_SENSOR, true);
			}
		});
		mntmDeselectAllRouters.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JMenuItem mntmDeselectAllFlyingObjects = new JMenuItem(
				"Deselect All Flying Objects");
		mnDeselectAll.add(mntmDeselectAllFlyingObjects);
		mntmDeselectAllFlyingObjects.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, Device.FLYING_OBJECT,
						true);
			}
		});
		mntmDeselectAllFlyingObjects.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JMenuItem mntmDeselectAllBase = new JMenuItem(
				"Deselect All Base Stations");
		mnDeselectAll.add(mntmDeselectAllBase);
		mntmDeselectAllBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllNodes(false, Device.BASE_STATION,
						true);
			}
		});
		mntmDeselectAllBase.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JMenuItem mntmDeselectAllMarkers = new JMenuItem("Deselect All Markers");
		mnDeselectAll.add(mntmDeselectAllMarkers);
		mntmDeselectAllMarkers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.setSelectionOfAllMarkers(false, Device.MARKER, true);
			}
		});
		mntmDeselectAllMarkers.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "deselect_node.png"));

		JSeparator separator_9 = new JSeparator();
		mnSelection.add(separator_9);

		JMenu mnNodes = new JMenu("Nodes");
		mnNodes.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "blank_badge_green.png"));
		menuBar.add(mnNodes);

		JMenuItem mntmAddSensor = new JMenuItem("Add Sensor");
		mntmAddSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.addNodeInMap('1');
			}
		});

//		JMenuItem mntmLoadSensors = new JMenuItem("Load Sensors/Targets");
//		mntmLoadSensors.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				WorldMap.loadCityNodes();
//			}
//		});
//		mnNodes.add(mntmLoadSensors);
		
		JMenuItem mntmLoadBuildings = new JMenuItem("Load Buildings");
		mntmLoadBuildings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BuildingList.loadFromOsm();
			}
		});
		mntmLoadBuildings.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "building.png"));
		mnNodes.add(mntmLoadBuildings);

		JSeparator separator_5 = new JSeparator();
		mnNodes.add(separator_5);
		mntmAddSensor.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "blank_badge_mauve.png"));
		mnNodes.add(mntmAddSensor);
		
				JMenuItem mntmAddBaseStation = new JMenuItem("Add Base Station");
				mntmAddBaseStation.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
						+ "blank_badge_orange.png"));
				mnNodes.add(mntmAddBaseStation);
		
		JMenuItem mntmAddGas = new JMenuItem("Add Gas");
		mntmAddGas.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "circle_orange.png"));
		mnNodes.add(mntmAddGas);

		JMenuItem mntmAddMobile = new JMenuItem("Add Mobile");
		mntmAddMobile.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "blank_badge_noir.png"));
		mnNodes.add(mntmAddMobile);

		JMenuItem mntmAddFlyingObjects = new JMenuItem("Add Flying Objects");
		mntmAddFlyingObjects.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "insects.png"));
		mnNodes.add(mntmAddFlyingObjects);

		JMenuItem mntmAddMarkers = new JMenuItem("Add Markers");
		mntmAddMarkers.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "marker_rounded_light_blue.png"));
		mnNodes.add(mntmAddMarkers);
		
		JMenu mnRandom = new JMenu("Random");
		mnRandom.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "randomp.png"));
		mnNodes.add(mnRandom);
		
		JMenuItem mntmAddSensors = new JMenuItem("Add 10 sensors");
		mntmAddSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RandomDevices.addRandomSensors(10);
			}
		});
		mnRandom.add(mntmAddSensors);
		
		JMenuItem mntmAddSensors_1 = new JMenuItem("Add 50 sensors");
		mntmAddSensors_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RandomDevices.addRandomSensors(50);
			}
		});
		mnRandom.add(mntmAddSensors_1);
		
		JMenuItem mntmAddSensors_3 = new JMenuItem("Add 100 sensors");
		mntmAddSensors_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RandomDevices.addRandomSensors(100);
			}
		});
		mnRandom.add(mntmAddSensors_3);
		
		JMenuItem mntmAddSensors_2 = new JMenuItem("Add 500 sensors");
		mntmAddSensors_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RandomDevices.addRandomSensors(500);
			}
		});
		mnRandom.add(mntmAddSensors_2);

		JSeparator separator_6 = new JSeparator();
		mnNodes.add(separator_6);

		JMenuItem mntmDevParameters = new JMenuItem("Device parameters");
		mntmDevParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDeviceParemeterWindow();
			}
		});
		mntmDevParameters.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "ui_menu_blue.png"));
		mnNodes.add(mntmDevParameters);

		JMenuItem mntmRouteFromMarkers = new JMenuItem("Route from markers");
		mntmRouteFromMarkers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MarkerList.generateOSMRouteFile();
			}
		});

		JMenuItem mntmMarkerParameters = new JMenuItem(
				"Marker Parameters");
		mntmMarkerParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFlyingObjectParemeterWindow();
			}
		});
		
		JMenuItem mntmRadioParameters = new JMenuItem("Radio parameters");
		mntmRadioParameters.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "ui_menu_blue.png"));
		mntmRadioParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!radioParametersWindow.isVisible()) {
					radioParametersWindow.setVisible(true);
					desktopPane.add(radioParametersWindow);
				}
				radioParametersWindow.toFront();
			}
		});
		mnNodes.add(mntmRadioParameters);
		mntmMarkerParameters.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "ui_menu_blue.png"));
		mnNodes.add(mntmMarkerParameters);
		mntmRouteFromMarkers.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "route.png"));
		mnNodes.add(mntmRouteFromMarkers);
		
		JMenuItem mntmInsertMarker = new JMenuItem("Insert markers");
		mntmInsertMarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//for(Marker marker : MarkerList.getMarkers()) {
				//	marker.insertMarker();
				//}
			}
		});
		mntmInsertMarker.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "route2.png"));
		mnNodes.add(mntmInsertMarker);
		
		JMenuItem mntmTransformMarkerTo = new JMenuItem("Transform markers to sensors");
		mntmTransformMarkerTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(Marker marker : MarkerList.getMarkers()) {
					marker.transformMarkerToSensor();
				}
			}
		});
		mntmTransformMarkerTo.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "marktosens.png"));
		mnNodes.add(mntmTransformMarkerTo);

		JSeparator separator_8 = new JSeparator();
		mnNodes.add(separator_8);

		JMenuItem mntmInitialize_1 = new JMenuItem("Initialize");
		mntmInitialize_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeviceList.initAlgoSelectedNodes();
			}
		});

//		JMenuItem mntmToOmnet = new JMenuItem("To OMNeT");
//		mntmToOmnet
//				.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "loopnone-1.png"));
//		mntmToOmnet.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				OmnetPp.omnetFileGeneration();
//			}
//		});
//		mnNodes.add(mntmToOmnet);

		JSeparator separator_11 = new JSeparator();
		mnNodes.add(separator_11);
		mntmInitialize_1.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "circle_grey.png"));
		mnNodes.add(mntmInitialize_1);

		JMenuItem mntmInitializeAll = new JMenuItem("Initialize All");
		mntmInitializeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeviceList.initAll();
				DeviceList.initAllGeoZones();
			}
		});
		mntmInitializeAll.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "circle_grey.png"));
		mnNodes.add(mntmInitializeAll);

		JMenu mnGraph = new JMenu("Graph");
		mnGraph.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "tree_diagramm.png"));
		menuBar.add(mnGraph);

		JMenuItem mntmSensorGraph = new JMenuItem("Sensor Graph");
		mntmSensorGraph
				.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "graph.png"));
		mntmSensorGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!infoWindow.isVisible()) {
					infoWindow.setVisible(true);
					desktopPane.add(infoWindow);
				}
				infoWindow.toFront();
				infoWindow.getTextPane().setText(
						DeviceList.displaySensorGraph().toString());
			}
		});
		mnGraph.add(mntmSensorGraph);

		JMenuItem mntmSensortargetGraph = new JMenuItem("Sensor/Target Graph");
		mntmSensortargetGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!infoWindow.isVisible()) {
					infoWindow.setVisible(true);
					desktopPane.add(infoWindow);
				}
				infoWindow.toFront();
				infoWindow.getTextPane().setText(
						DeviceList.displaySensorTargetGraph().toString());
			}
		});
		mntmSensortargetGraph.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "graph.png"));
		mnGraph.add(mntmSensortargetGraph);

		JMenu mnResolution = new JMenu("Solver");
		mnResolution.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "edu_mathematics-1.png"));
		menuBar.add(mnResolution);

		JMenuItem mntmMinSetCover = new JMenuItem("Sensor Coverage");
		mntmMinSetCover.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "edu_mathematics-1.png"));
		mntmMinSetCover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SensorSetCover.sensorSetCover();
			}
		});
		mnResolution.add(mntmMinSetCover);

		JMenuItem mntmNewMenuItem = new JMenuItem("Target Coverage");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SensorSetCover.sensorTargetSetCover() ;				
			}
		});
		mntmNewMenuItem.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "edu_mathematics-1.png"));
		mnResolution.add(mntmNewMenuItem);
		
		JMenuItem mntmTargetCoverageth = new JMenuItem("Target Coverage (Th)");
		mntmTargetCoverageth.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "edu_mathematics-1.png"));
		mntmTargetCoverageth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				(new SensorTargetCoverageRun()).start();
			}
		});
		mnResolution.add(mntmTargetCoverageth);
		
		JMenuItem mntmScheduling = new JMenuItem("Scheduling");
		mntmScheduling.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "edu_mathematics-1.png"));
		mntmScheduling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CharlySchedul.run();		
			}
		});
		mnResolution.add(mntmScheduling);
		
		JMenuItem mntmnetcenter = new JMenuItem("Network Center");		
		mntmnetcenter.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmnetcenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkCenter netcenter = new NetworkCenter() ;
				netcenter.start();			
			}
		});
		mnResolution.add(mntmnetcenter);
		
		
		JMenu mnEnvelope = new JMenu("Envelope");
		mnResolution.add(mnEnvelope);
		
		JMenuItem mntmBorder = new JMenuItem("P-Envelope");		
		mntmBorder.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmBorder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkEnvelopeP npb = new NetworkEnvelopeP(true) ;
				npb.start();			
			}
		});
		mnEnvelope.add(mntmBorder);
		
		JMenuItem mntmBorder2 = new JMenuItem("P-Envelope With Connections");		
		mntmBorder2.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmBorder2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkEnvelopeP npb = new NetworkEnvelopeP(false) ;
				npb.start();			
			}
		});
		mnEnvelope.add(mntmBorder2);
		
		mnEnvelope.addSeparator();
		
		JMenuItem mntmJarvis = new JMenuItem("Run Jarvis Algorithm");		
		mntmJarvis.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmJarvis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EnvelopeJarvis envJarvis = new EnvelopeJarvis() ;
				envJarvis.start();			
			}
		});
		mnEnvelope.add(mntmJarvis);
		
		JMenuItem mntmRunGiftWrap = new JMenuItem("Run LPCN Algorithm");		
		mntmRunGiftWrap.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmRunGiftWrap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lpcn = new EnvelopeLPCN() ;
				lpcn.start();			
			}
		});
		mnEnvelope.add(mntmRunGiftWrap);
		
		JMenuItem mntmStopGiftWrap = new JMenuItem("Stop LPCN Algorithm");		
		mntmStopGiftWrap.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmStopGiftWrap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lpcn!=null) 
					lpcn.stopAlgorithm();			
			}
		});
		mnEnvelope.add(mntmStopGiftWrap);
		
		JMenuItem mntmRunLpcnMobile= new JMenuItem("Run LPCN Mobile");		
		mntmRunLpcnMobile.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmRunLpcnMobile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lpcnm = new EnvelopeLPCNMobile() ;
				lpcnm.start();			
			}
		});
		mnEnvelope.add(mntmRunLpcnMobile);
		
		JMenuItem mntmStopLpcnMobile= new JMenuItem("Stop LPCN Mobile");		
		mntmStopLpcnMobile.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmStopLpcnMobile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lpcnm!=null) 
					lpcnm.stopAlgorithm();	
			}
		});
		mnEnvelope.add(mntmStopLpcnMobile);
		
		mnEnvelope.addSeparator();
		
		JMenuItem mntmBorder4 = new JMenuItem("Run Graham Algo");		
		mntmBorder4.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmBorder4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkEnvelopeC npb = new NetworkEnvelopeC() ;
				npb.start();			
			}
		});
		mnEnvelope.add(mntmBorder4);
		
		JMenuItem mntmBorder5 = new JMenuItem("Run Personal Algo");		
		mntmBorder5.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
		mntmBorder5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NetworkPerso npb = new NetworkPerso() ;
				npb.start();			
			}
		});
		mnEnvelope.add(mntmBorder5);
		
//		JMenuItem mntmGpsrb = new JMenuItem("GPSR-B Envelope");
//		mntmGpsrb.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
//		mntmGpsrb.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				GpsrB gpsrb = new GpsrB() ;
//				gpsrb.start();	
//			}
//		});
//		mnEnvelope.add(mntmGpsrb);
		
		
//		JMenuItem mntmEnvelopeAngle = new JMenuItem("Envelope By Angle");		
//		mntmEnvelopeAngle.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "edu_mathematics-1.png"));
//		mntmEnvelopeAngle.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				NetworkEnvelopeByAngle nba = new NetworkEnvelopeByAngle() ;
//				nba.start();			
//			}
//		});
//		mnEnvelope.add(mntmEnvelopeAngle);		
		
		JSeparator separator_13 = new JSeparator();
		mnResolution.add(separator_13);
		
		JMenuItem mntmProxyParameters = new JMenuItem("Proxy parameters");
		mntmProxyParameters.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "activity_window.png"));
		mnResolution.add(mntmProxyParameters);
		mntmProxyParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!solverProxyParamsWindow.isVisible()) {
					desktopPane.add(solverProxyParamsWindow);
					solverProxyParamsWindow.setVisible(true);
				}
				solverProxyParamsWindow.toFront();
			}
		});

		JMenu mnSimulation = new JMenu("Simulation");
		mnSimulation.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "run.png"));
		menuBar.add(mnSimulation);

		JMenuItem mntmSimulate = new JMenuItem("Simulate Agent");
		mntmSimulate.setAccelerator(KeyStroke.getKeyStroke('s'));
		mntmSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.simulate();
			}
		});
		mntmSimulate.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "flag_green.png"));
		mnSimulation.add(mntmSimulate);

		JMenuItem mntmSimulateAll = new JMenuItem("Simulate All Agents");
		mntmSimulateAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.SHIFT_MASK));
		mntmSimulateAll.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "flag_green2.png"));
		mntmSimulateAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WorldMap.simulateAll();
			}
		});		
		mnSimulation.add(mntmSimulateAll);
		
		JMenuItem mntmSimulateSensors = new JMenuItem("Simulate Sensors");
		mntmSimulateSensors.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "flag_green2.png"));
		mntmSimulateSensors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WorldMap.simulateSensors();
			}
		});
		mnSimulation.add(mntmSimulateSensors);
		
		JMenuItem mntmSimulateMobiles = new JMenuItem("Simulate Mobiles");
		mntmSimulateMobiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.simulateMobiles();
			}
		});
		mntmSimulateMobiles.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "flag_green2.png"));
		mnSimulation.add(mntmSimulateMobiles);
		

		JMenuItem mntmStopSimulation = new JMenuItem("Stop simulation");
		mntmStopSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DeviceList.stopSimulation();
			}
		});
		mntmStopSimulation.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "flag_red.png"));
		mnSimulation.add(mntmStopSimulation);
		
				JSeparator separator_7 = new JSeparator();
				mnSimulation.add(separator_7);
		
				JMenuItem mntmInitialize = new JMenuItem("Initialize All");
				mnSimulation.add(mntmInitialize);
				mntmInitialize.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DeviceList.initAll();
						//GeoZoneList.init();
					}
				});
				mntmInitialize.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
						+ "circle_grey.png"));
		
//		JSeparator separator_14 = new JSeparator();
//		mnSimulation.add(separator_14);
//		
//		JMenuItem mntmSimulateTracking = new JMenuItem("Simulate Tracking");
//		mntmSimulateTracking.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				TrackingPointsList.simulate();
//			}
//		});
//		mnSimulation.add(mntmSimulateTracking);
//		
//		JMenuItem mntmSimulateTrackingreal = new JMenuItem("Simulate Tracking (real)");
//		mntmSimulateTrackingreal.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				TrackerReader tr = new TrackerReader() ;
//				tr.start();
//			}
//		});
//		mnSimulation.add(mntmSimulateTrackingreal);
//		
//		JMenuItem mntmSimulateTrackingKhaoula = new JMenuItem("Simulate Tracking Khaoula");
//		mntmSimulateTrackingKhaoula.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				realTrackingDialog.setLocationRelativeTo(mainFrame);
//				realTrackingDialog.setVisible(true);
//				
//				String targetId = realTrackingDialog.getTargetId();
//				String trackerId = realTrackingDialog.getTrackerId();
//                if (trackerId != null && !trackerId.isEmpty() && targetId != null
//        				&& !targetId.isEmpty()) {
//                	DeviceList.startRealTrackingSimulation(trackerId,targetId);
//                }
//			}
//		});
//		mnSimulation.add(mntmSimulateTrackingKhaoula);
		
		JSeparator separator_15 = new JSeparator();
		mnSimulation.add(separator_15);
		
		JMenuItem mntmRunFaultInjector = new JMenuItem("Run Fault Injection");
		mntmRunFaultInjector.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "runinjection.png"));
		mntmRunFaultInjector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				faultInjector = new FaultInjector();
				faultInjector.start();
			}
		});
		mnSimulation.add(mntmRunFaultInjector);
		
		JMenuItem mntmStopFaultInjector = new JMenuItem("Stop Fault Injection");
		mntmStopFaultInjector.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "stopinjection.png"));
		mntmStopFaultInjector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(faultInjector != null) 
				faultInjector.stopInjection();
			}
		});
		mnSimulation.add(mntmStopFaultInjector);
		mnSimulation.add(new JSeparator());

		JMenuItem mntmCreateComScenario = new JMenuItem("Communication script");
		mntmCreateComScenario.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.ALT_MASK));
		mntmCreateComScenario.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "stylo.png"));
		mnSimulation.add(mntmCreateComScenario);
		mntmCreateComScenario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File comFiles = new File(Project.getProjectScriptPath());
				String[] c = comFiles.list();
				ScriptWindow.txtLoadFileName.removeAllItems();
				ScriptWindow.txtLoadFileName.addItem("New script ...");
				for (int i = 0; i < c.length; i++) {
					ScriptWindow.txtLoadFileName.addItem(c[i]);
				}

				if (!comScriptWindow.isVisible()) {
					desktopPane.add(comScriptWindow);
					comScriptWindow.setVisible(true);
				}
				comScriptWindow.toFront();
			}
		});

		JMenuItem mntmSimulation = new JMenuItem("WSN Simulation");
		mntmSimulation.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
		mntmSimulation.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "settings_right_rest.png"));
		mntmSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.deSimulation();
				if (!wsnSimWindow.isVisible()) {
					desktopPane.add(wsnSimWindow);
					wsnSimWindow.setVisible(true);
				}
				wsnSimWindow.toFront();
			}
		});

		JSeparator separator_12 = new JSeparator();
		mnSimulation.add(separator_12);
		mnSimulation.add(mntmSimulation);

		JSeparator separator_4 = new JSeparator();
		mnSimulation.add(separator_4);
		
		JMenuItem mntmGenerateArduinoCode = new JMenuItem("Generate Arduino Code");
		mntmGenerateArduinoCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Arduino.generateCode();
			}
		});
		
		JMenuItem mntmExternalVisualisation = new JMenuItem("External Visualisation");
		mntmExternalVisualisation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!visualisationWindow.isVisible()) {
					desktopPane.add(visualisationWindow);
					visualisationWindow.setVisible(true);
				}
				visualisationWindow.toFront();
			}
		});
		mnSimulation.add(mntmExternalVisualisation);
		mnSimulation.add(mntmGenerateArduinoCode);
		
		JMenu mnMap = new JMenu("Map");
		mnMap.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "logo_cap_carbon.png"));
		menuBar.add(mnMap);
		
		JRadioButtonMenuItem rdbtnmntmClassic = new JRadioButtonMenuItem("OSM Standard (color)");
		rdbtnmntmClassic.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		buttonGroup.add(rdbtnmntmClassic);
		rdbtnmntmClassic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				changeOsmTiles("http://a.tile.openstreetmap.org/");
			}
		});
		
		JRadioButtonMenuItem rdbtnmntmLight = new JRadioButtonMenuItem("OSM Light");
		buttonGroup.add(rdbtnmntmLight);
		rdbtnmntmLight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeOsmTiles("http://a.basemaps.cartocdn.com/light_all/");			
			}
		});
		
		JRadioButtonMenuItem rdbtnmntmStandard = new JRadioButtonMenuItem("Local (Standard)");
		rdbtnmntmStandard.setSelected(true);
		buttonGroup.add(rdbtnmntmStandard);
		rdbtnmntmStandard.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		rdbtnmntmStandard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLocalTiles("cuptile_std.png");				
			}
		});
		mnMap.add(rdbtnmntmStandard);
		rdbtnmntmLight.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		mnMap.add(rdbtnmntmLight);
		
		JSeparator separator_17 = new JSeparator();
		mnMap.add(separator_17);
		
		JRadioButtonMenuItem rdbtnmntmLocaldarkBlue = new JRadioButtonMenuItem("Local (Dark Blue)");
		buttonGroup.add(rdbtnmntmLocaldarkBlue);
		rdbtnmntmLocaldarkBlue.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		rdbtnmntmLocaldarkBlue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLocalTiles("cuptile_dark_blue.png");
			}
		});
		
		JRadioButtonMenuItem rdbtnmntmLocallines = new JRadioButtonMenuItem("Local (Lines)");
		buttonGroup.add(rdbtnmntmLocallines);
		rdbtnmntmLocallines.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		rdbtnmntmLocallines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLocalTiles("cuptile_lines.png");
			}
		});
		mnMap.add(rdbtnmntmLocallines);
		mnMap.add(rdbtnmntmLocaldarkBlue);
		
		JRadioButtonMenuItem rdbtnmntmLocaldots = new JRadioButtonMenuItem("Local (Dots)");
		buttonGroup.add(rdbtnmntmLocaldots);
		rdbtnmntmLocaldots.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		rdbtnmntmLocaldots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLocalTiles("cuptile_dots.png");
			}
		});
		mnMap.add(rdbtnmntmLocaldots);
		
		JRadioButtonMenuItem rdbtnmntmLocalwhite = new JRadioButtonMenuItem("Local (White)");
		buttonGroup.add(rdbtnmntmLocalwhite);
		rdbtnmntmLocalwhite.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		rdbtnmntmLocalwhite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeLocalTiles("cuptile_white.png");
			}
		});
		mnMap.add(rdbtnmntmLocalwhite);
		
		JSeparator separator_18 = new JSeparator();
		mnMap.add(separator_18);
		
		JRadioButtonMenuItem rdbtnmntmOsm = new JRadioButtonMenuItem("OSM 2");
		rdbtnmntmOsm.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		buttonGroup.add(rdbtnmntmOsm);
		rdbtnmntmOsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeOsmTiles("http://otile1.mqcdn.com/tiles/1.0.0/osm/");
			}
		});
		mnMap.add(rdbtnmntmOsm);
		
		JRadioButtonMenuItem mntmLocal = new JRadioButtonMenuItem("OSM Dark");
		mntmLocal.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		buttonGroup.add(mntmLocal);
		mntmLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//changeNetTiles("http://a.tiles.wmflabs.org/bw-mapnik/");
				changeOsmTiles("http://a.basemaps.cartocdn.com/dark_all/");
			}
		});
		mnMap.add(mntmLocal);
		
		JRadioButtonMenuItem mntmTransport = new JRadioButtonMenuItem("OSM Transport");
		mntmTransport.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		buttonGroup.add(mntmTransport);
		mntmTransport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeOsmTiles("http://a.tile2.opencyclemap.org/transport/");
			}
		});
		mnMap.add(mntmTransport);
		mnMap.add(rdbtnmntmClassic);
		
		JRadioButtonMenuItem rdbtnmntmCyclic = new JRadioButtonMenuItem("OSM Cycle");
		rdbtnmntmCyclic.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		buttonGroup.add(rdbtnmntmCyclic);
		rdbtnmntmCyclic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeOsmTiles("http://a.tile.opencyclemap.org/cycle/");
			}
		});
		mnMap.add(rdbtnmntmCyclic);
		
		JSeparator separator_21 = new JSeparator();
		mnMap.add(separator_21);
		
		JRadioButtonMenuItem rdbtnmntmGoogleMap = new JRadioButtonMenuItem("Google Map");
		rdbtnmntmGoogleMap.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		buttonGroup.add(rdbtnmntmGoogleMap);
		rdbtnmntmGoogleMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// h = roads only
				// m = standard roadmap
				// p = terrain
				// r = somehow altered roadmap
				// s = satellite only
				// t = terrain only
				// y = hybrid
				changeGoogleTiles("http://mt0.google.com/vt/lyrs=m&hl=en");
			}
		});
		mnMap.add(rdbtnmntmGoogleMap);
		
		JRadioButtonMenuItem rdbtnmntmSatellit = new JRadioButtonMenuItem("Google Map (Sat)");
		rdbtnmntmSatellit.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "geo.png"));
		buttonGroup.add(rdbtnmntmSatellit);
		rdbtnmntmSatellit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeGoogleTiles("http://mt0.google.com/vt/lyrs=s&hl=en");
			}
		});
		mnMap.add(rdbtnmntmSatellit);
		
		JSeparator separator_16 = new JSeparator();
		mnMap.add(separator_16);
		
		JMenu mnCenter = new JMenu("City");
		mnMap.add(mnCenter);
		
		cityTextEdit = new JTextField();
		cityTextEdit.setText("Brest");
		mnCenter.add(cityTextEdit);
		cityTextEdit.setColumns(10);
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s = cityTextEdit.getText();
				double d1 = City.getCityCenter(s)[0];
				double d2 = City.getCityCenter(s)[1];				
				CupCarbonMap.getMap().setCenterPosition(new GeoPosition(d1, d2));
			}
		});
		mnCenter.add(btnOk);
		
		JMenu mnPerso = new JMenu("Perso");
		mnPerso.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "personal-icon.png"));
		menuBar.add(mnPerso);
		
		//------------------------------------------------------------
		//------------------------------------------------------------
		//	My Action Menu Item
		//------------------------------------------------------------
		//------------------------------------------------------------		
		JMenuItem mntmMyAction = new JMenuItem("My Action");
		mntmMyAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Think about importing the package where your class is created
				MyClass myclass = new MyClass();
				myclass.start();
			}
		});
		mnPerso.add(mntmMyAction);

		//------------------------------------------------------------
		//------------------------------------------------------------
		
		//------------------------------------------------------------
		
		//------------------------------------------------------------
		//	My Action Menu Item
		//------------------------------------------------------------
		//------------------------------------------------------------		
		JMenuItem mntmExample = new JMenuItem("Example");
		mntmExample.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ExampleClass exc = new ExampleClass();
				exc.start();
			}
		});
		mnPerso.add(mntmExample);
		
		JMenuItem monMenuAlgo = new JMenuItem("Mon Algo");
		monMenuAlgo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MonAlgoClass monAlgo = new MonAlgoClass();
				monAlgo.start();
			}
		});
		mnPerso.add(monMenuAlgo);

		//------------------------------------------------------------
		//------------------------------------------------------------
		
		JMenu mnHelp = new JMenu("Help");
		mnHelp.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "symbol_help.png"));
		menuBar.add(mnHelp);

		JMenuItem mntmAboutCupcarbon = new JMenuItem("About CupCarbon");
		mntmAboutCupcarbon.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "symbol_information.png"));
		mntmAboutCupcarbon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (aboutBox == null) {
					aboutBox = new AboutCupCarbon();
					aboutBox.setLocationRelativeTo(mainFrame);
				}
				aboutBox.setVisible(true);
			}
		});
		
		JMenuItem mntmHelp = new JMenuItem("Commands");
		mntmHelp.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "symbol_information.png"));
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (!helpWindow.isVisible()) {
					desktopPane.add(helpWindow);
					helpWindow.setVisible(true);
				}
				helpWindow.toFront();
			}
		});
		mnHelp.add(mntmHelp);
		mnHelp.add(mntmAboutCupcarbon);

		JToolBar toolBar = new JToolBar();
		mainFrame.getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton btnSensor = new JButton("1 Sensor");
		btnSensor.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "blank_badge_mauve.png"));
		btnSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				WorldMap.addNodeInMap('1');
			}
		});
		toolBar.add(btnSensor);

		JButton btnGas = new JButton("2 Gas");
		btnGas.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "circle_orange.png"));
		btnGas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WorldMap.addNodeInMap('2');
			}
		});
		toolBar.add(btnGas);

		JButton btnMobile = new JButton("6 Mobile");
		btnMobile.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "blank_badge_noir.png"));
		btnMobile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.addNodeInMap('6');
			}
		});

		JButton btnBaseStation = new JButton("5 Base Station");
		btnBaseStation.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "blank_badge_orange.png"));
		btnBaseStation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.addNodeInMap('5');
			}
		});
		
		JButton button_1 = new JButton("4 Media Sensor");
		button_1.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "blank_badge_cam.png"));
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldMap.addNodeInMap('4');
			}
		});
		toolBar.add(button_1);
		toolBar.add(btnBaseStation);
		toolBar.add(btnMobile);

		JButton btnMarker = new JButton("8 Marker");
		btnMarker.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "marker_rounded_light_blue.png"));
		btnMarker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WorldMap.addNodeInMap('8');
			}
		});
		toolBar.add(btnMarker);
		
		JButton btnQuickSimulation = new JButton("");
		btnQuickSimulation.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "run_sim_quick.png"));
				
		btnQuickSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WisenSimulationWindow.quickRun();
			}
		});
		
		JButton btnConnexions = new JButton("Connexions");
		btnConnexions.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "connexion.png"));
		btnConnexions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeviceList.resetPropagations();
				MapLayer.getMapViewer().repaint();
			}
		});
		toolBar.add(btnConnexions);
		
		JButton btnPropagations = new JButton("Propagations");
		btnPropagations.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "propagation.png"));
		btnPropagations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeviceList.calculatePropagations();
				MapLayer.getMapViewer().repaint();
			}
		});
		toolBar.add(btnPropagations);
		
		JButton btnVisibility = new JButton("Visibility");
		btnVisibility.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH + "visibility.png"));
		btnVisibility.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Not available in this version.");
				//System.out.println("Number of Buildings : "+BuildingList.size());
				//VisibilityLauncher vl = new VisibilityLauncher();
				//vl.start();				
			}
		});
		toolBar.add(btnVisibility);
		toolBar.add(btnQuickSimulation);
		
		JButton button = new JButton("");
		button.setIcon(new ImageIcon(CupCarbonParameters.IMGPATH
				+ "square_red.png"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WisenSimulationWindow.stopSimulation();
			}
		});
		toolBar.add(button);

		panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(2, 2, 2, 2));
		toolBar.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		
		labelMag = new JLabel("    ");
		labelMag.setFont(new Font("Arial", Font.PLAIN, 12));
		panel_1.add(labelMag);

		JPanel panel = new JPanel();
		panel_1.add(panel);
		//panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		lblNodesNumber = new JLabel(" N : ");
		lblNodesNumber.setFont(new Font("Arial", Font.PLAIN, 12));
		panel.add(lblNodesNumber);

		label = new JLabel("0");
		label.setFont(new Font("Arial", Font.PLAIN, 12));
		panel.add(label);

		JPanel panel_2 = new JPanel();
		//panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

		JLabel lblNewLabel = new JLabel(" | Ag. Speed:  ");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		panel_2.add(lblNewLabel);

		sspeedLabel = new JLabel("100  ");
		sspeedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		panel_2.add(sspeedLabel);
				
		lblSimulation.setFont(new Font("Arial", Font.PLAIN, 12));
		toolBar.add(lblSimulation);

		cupCarbonMap = new CupCarbonMap();
		cupCarbonMap.setBounds(0, 0, 1000, 634);
		desktopPane.setLayer(cupCarbonMap, 0);
		cupCarbonMap.getContentPane().setBackground(Color.LIGHT_GRAY);
		cupCarbonMap.getContentPane().setFont(new Font("Arial", Font.PLAIN, 12));
		cupCarbonMap.setFrameIcon(new ImageIcon("images/cupcarbon_logo_small.png"));
		cupCarbonMap.setVisible(true);

		mainFrame.getContentPane().add(desktopPane, BorderLayout.CENTER);
		desktopPane.setBackground(new Color(173, 216, 230));
		desktopPane.setLayout(null);
		desktopPane.add(cupCarbonMap);

		try {

			cupCarbonMap.setMaximum(true);

		} catch (PropertyVetoException ex) {

			ex.printStackTrace();

		}
		 //moveToFront(cupCarbonMap);
//		try {
//			Class.forName("com.virtualys.cupcarbon.communication.Communication");
//			com.virtualys.cupcarbon.communication.Communication.initialize();
//		} catch(ClassNotFoundException e) {
//			System.out.println("Unknowed exception : " + e.getMessage());
//		}
//		//_OM_Vlys_end	
	}

	private void openDeviceParemeterWindow() {
		File gpsFiles = new File(Project.getProjectGpsPath());
		String[] s = gpsFiles.list();
		if (s == null)
			s = new String[1];
		DeviceParametersWindow.gpsPathNameComboBox.removeAllItems();
		DeviceParametersWindow.gpsPathNameComboBox.addItem("");
		for (int i = 0; i < s.length; i++) {
			DeviceParametersWindow.gpsPathNameComboBox.addItem(s[i]);
		}

		File comFiles = new File(Project.getProjectScriptPath());
		s = comFiles.list();
		if (s == null)
			s = new String[1];
		DeviceParametersWindow.scriptComboBox.removeAllItems();
		DeviceParametersWindow.scriptComboBox.addItem("");
		for (int i = 0; i < s.length; i++) {
			DeviceParametersWindow.scriptComboBox.addItem(s[i]);
		}

		if (!deviceParametersWindow.isVisible()) {
			deviceParametersWindow.setVisible(true);
			desktopPane.add(deviceParametersWindow);
		}
		deviceParametersWindow.toFront();
	}

	private void openFlyingObjectParemeterWindow() {
		if (!gpsWindow.isVisible()) {
			desktopPane.add(gpsWindow);
			gpsWindow.setVisible(true);
		}
		gpsWindow.toFront();
	}

	public static void updateInfos() {
		label.setText("" + DeviceList.size() + "  ");
		sspeedLabel.setText("" + Device.moveSpeed + "  ");
	}
	
	public void changeGoogleTiles(String s) {
		WorldMap.gmap = true ;
		WorldMap.local = false ;
		WorldMap.tileUrl = s;						
		MapLayer.getMapViewer().repaint();
	}
	
	public void changeOsmTiles(String s) {
		WorldMap.gmap = false ;
		WorldMap.local = false ;
		WorldMap.tileUrl = s;						
		MapLayer.getMapViewer().repaint();
	}
	
	public void changeLocalTiles(String s) {
		WorldMap.gmap = false ;
		WorldMap.local = true ;
		WorldMap.tileName = s;
		Refresher ref = new Refresher();
		ref.start();
	}
	
	public static void setProxy() {
		//System.getProperties().put("http.proxySet", "true"); 
		//System.getProperties().put("http.proxyPort", "3128");
		//System.getProperties().put("http.proxyHost", "proxyubo.univ-brest.fr");
		//System.getProperties().put("http.proxyHost", "193.52.48.67");
		System.getProperties().put("http.proxySet", SolverProxyParams.proxy); 
		System.getProperties().put("http.proxyPort", SolverProxyParams.port); 
		System.getProperties().put("http.proxyHost", SolverProxyParams.host);
	}
}
