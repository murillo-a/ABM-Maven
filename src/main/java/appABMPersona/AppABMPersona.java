package appABMPersona;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class AppABMPersona {

	public static void main(String[] args) {

		System.out.println("SISTEMA DE PERSONAS (ABM)");
		System.out.println("=========================\n");
		Connection conexion = null;
		try {
			conexion = AdminBD.obtenerConexion();
			Scanner sc = new Scanner(System.in);

			int opcion = mostrarMenu(sc);
			while (opcion != 0) {

				switch (opcion) {
				case 1:
					alta(conexion, sc);
					break;
				case 2:
					modificacion(conexion, sc);
					break;
				case 3:
					baja(conexion, sc);
					break;
				case 4:
					listado(conexion);
					break;
				case 5:
					buscarRegistro(conexion, sc);
					break;
				case 6:
					venta(conexion, sc);
					break;
				case 0:

					break;
				default:
					System.out.println("\nIngrese un numero válido.\n");
					break;
				}
				opcion = mostrarMenu(sc);
			}

			conexion.close();
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Ha ocurrido un error.");
		}
		System.out.println("Fin del programa.");
		
	}

	private static void venta(Connection conexion, Scanner sc) {
		// TODO Auto-generated method stub
		System.out.print("VENTA\nIngrese ID de la persona: ");
		int id = sc.nextInt();
		System.out.print("Ingrese importe de la venta: ");
		float importe = sc.nextFloat();
		Date dateVenta = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fechaString = sdf.format(dateVenta);
		
		String sql = "INSERT INTO venta (FECHA, IMPORTE, ID_PERSONA) VALUES ('" + fechaString + "', " + importe + ", " + id + ");";
		sqlUpdate(conexion, sql);
		
		// TODO mostrar ID de venta
		System.out.println("Se ha registrado la venta.");
		
	}

	private static void buscarRegistro(Connection conexion, Scanner sc) {

		int opcion;
		do {

			System.out.println(
					"Ingrese opcion para buscar por\n1: nombre | 2: edad | otro número para cancelar búsqueda");
			opcion = sc.nextInt();
			switch (opcion) {
			case 1:
				String nombre = pedirNombre(sc);
				buscarPorNombre(conexion, nombre);
				break;
			case 2:
				int edad = pedirEdad(sc);
				buscarPorEdad(conexion, edad);
				break;
			default:
				System.out.println("Ingrese un número de opción válido.");
				break;

			}
			System.out.println("Ingrese 1 para buscar otro registro, otro número para volver al menu: ");
			opcion = sc.nextInt();
		} while (opcion == 1);

	}

	private static void buscarPorEdad(Connection conexion, int edad) {
		String sql = "SELECT * FROM persona WHERE EDAD = '" + edad + "' ;";
		showQueryResults(conexion, sql);

	}

	private static void showQueryResults(Connection conexion, String sql) {
		Statement stmt;
		try {
			stmt = conexion.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			boolean vacio = true;
			while (rs.next()) {
				System.out.println(rs.getInt("ID") + " " + rs.getString("NOMBRE") + " " + rs.getInt("EDAD") + " "
						+ rs.getString("FECHA_NACIMIENTO"));
			} 
			if (vacio) {
				System.out.println("No se han encontrado registros con estos parámetros.");
			}
		} catch (SQLException e) {
			System.out.println("Ha ocurrido un error obteniendo el registro.");
		}
	}

	private static int pedirEdad(Scanner sc) {
		System.out.print("Ingrese la edad: ");
		int edad = sc.nextInt();
		return edad;
	}

	private static void buscarPorNombre(Connection conexion, String nombre) {
		String sql = "SELECT * FROM persona WHERE nombre LIKE '" + nombre + "%' ;";
		showQueryResults(conexion, sql);

	}

	private static String pedirNombre(Scanner sc) {
		System.out.print("Ingrese el nombre: ");
		String nombre = sc.next();
		return nombre;
	}

	private static void listado(Connection conexion) {
		System.out.println();
		System.out.println("LISTADO:");
		System.out.println("ID - NOMBRE - EDAD - F.NACIMIENTO\n");
		String sql = ("SELECT * FROM PERSONA");
		showQueryResults(conexion, sql);

		System.out.println("\nFIN LISTADO------------\n");

	}

	private static void baja(Connection conexion, Scanner sc) {
		System.out.print("Ingrese ID para dar de baja: ");
		int id = sc.nextInt();
		String sql = "SELECT * FROM persona WHERE ID = '" + id + "';";
		showQueryResults(conexion, sql);
		System.out.print("Desea dar de baja a esta persona?\nIngrese 1 para continuar, otro número para ver menu: ");
		int opcion = sc.nextInt();
		if (opcion == 1) {

			sql = "DELETE FROM persona WHERE ID = '" + id + "';";
			sqlUpdate(conexion, sql);
			System.out.println("Se ha eliminado al registro #" + id + ".\n");
		}

	}

	private static void sqlUpdate(Connection conexion, String sql) {
		Statement stmt;
		try {
			stmt = conexion.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Ha ocurrido un error en la modificación/baja.");
		}

	}

	private static void modificacion(Connection conexion, Scanner sc) {

		int opcion;
		do {
			System.out.print("Ingrese ID de persona para modificar registro: ");
			int id = sc.nextInt();
			String sql = "SELECT * FROM persona WHERE ID = '" + id + "';";
			System.out.println("Usted ha seleccionado a:");
			showQueryResults(conexion, sql);
			System.out.println(
					"Ingrese 1 para modificar nombre, 2 para modificar fecha de nacimiento/edad, otro número para cancelar: ");
			opcion = sc.nextInt();
			switch (opcion) {
			case 1:
				System.out.print("Ingrese el nuevo nombre: ");
				String nombre = sc.next();
				sql = "UPDATE persona SET nombre = '" + nombre + "' WHERE ID =" + id;
				sqlUpdate(conexion, sql);
				System.out.println("Se ha modificado el nombre.");
				break;
			case 2:
				System.out.print("Ingrese la nueva fecha de nacimiento (dd/mm/aaaa): ");
				String fechaStr = sc.next();
				SimpleDateFormat sdfDMY = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");

				Date fechaNac;
				try {
					fechaNac = sdfDMY.parse(fechaStr);
					fechaStr = sdfYMD.format(fechaNac);

					int edad = calcularEdad(fechaNac);
					sql = "UPDATE persona SET FECHA_NACIMIENTO = '" + fechaStr + "', EDAD = " + edad + " WHERE ID ="
							+ id;
					sqlUpdate(conexion, sql);

				} catch (ParseException e) {
					System.out.println("Ha ocurrido un error en la modificación.");
				}

				System.out.println("Se ha modificado la fecha de nacimiento/edad.");

				System.out.print("Ingrese 1 para modificar otro registro, otro número para volver al menu: ");
				opcion = sc.nextInt();
				break;
			default:
				break;
			}

		} while (opcion == 1);
	}

	private static void alta(Connection conexion, Scanner sc) {
		System.out.print("ALTA DE PERSONA\nIngrese nombre: ");
		String nombre = sc.next();
		System.out.print("Ingrese fecha nacimiento (dd/mm/aaaa): ");
		String fechaNacimientoString = sc.next();

		SimpleDateFormat sdfDMY = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date fechaNac = sdfDMY.parse(fechaNacimientoString);
			fechaNacimientoString = sdfYMD.format(fechaNac);
			int edad = calcularEdad(fechaNac);
			String sql = "INSERT INTO PERSONA (NOMBRE, EDAD, FECHA_NACIMIENTO) VALUES ('" + nombre + "'," + edad + ",'"
					+ fechaNacimientoString + "') ;";
			sqlUpdate(conexion, sql);
			String msj = "Se ha añadido a " + nombre + ".";
			System.out.println(msj);

		} catch (ParseException e) {
			System.out.println("Ha ocurrido un error en el alta.");
		}

	}

	private static int calcularEdad(Date fechaNac) {
		GregorianCalendar gc = new GregorianCalendar();
		GregorianCalendar hoy = new GregorianCalendar();
		gc.setTime(fechaNac);
		int anioActual = hoy.get(Calendar.YEAR);
		int anioNacim = gc.get(Calendar.YEAR);

		int mesActual = hoy.get(Calendar.MONTH);
		int mesNacim = gc.get(Calendar.MONTH);

		int diaActual = hoy.get(Calendar.DATE);
		int diaNacim = gc.get(Calendar.DATE);

		int dif = anioActual - anioNacim;

		if (mesActual < mesNacim) {
			dif -= 1;
		} else {
			if (mesActual == mesNacim && diaActual < diaNacim) {
				dif -= 1;
			}
		}

		return dif;
	}

	private static int mostrarMenu(Scanner sc) {

		System.out.println("MENU OPCIONES:\n");
		System.out.println("1: ALTA | 2: MODIFICACION | 3: BAJA | 4: LISTADO | 5: BÚSQUEDA | 6: VENTA | 0: SALIR");
		int opcion;
		opcion = sc.nextInt();
		return opcion;
	}
}
