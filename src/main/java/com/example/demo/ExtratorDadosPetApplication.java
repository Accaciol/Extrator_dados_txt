package com.example.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootApplication
public class ExtratorDadosPetApplication {

	// static String jarDirectory = System.getProperty("user.dir");
	static String jarDirectory = "H:\\downloads\\BiaTXT";

	static String userHome = System.getProperty("user.home");
	static String filePath = jarDirectory + File.separator + "DadosExtraidos" + File.separator;
//		static String fileTXTPath = System.getProperty("user.home") + File.separator + "DadosExtraidos" + File.separator + "extracted_results.txt";
	static String fileTXTPath = "H:\\downloads\\BiaTXT\\DadosExtraidos" + File.separator + "extracted_results.txt";

	public static void main(String[] args) {
		SpringApplication.run(ExtratorDadosPetApplication.class, args);
		System.out.println("filePath" + filePath);
		System.out.println("fileTXTPath" + fileTXTPath);

		// // Get the current working directory (where the JAR is located)
//		    String jarDirectory = System.getProperty("user.dir");

		// Specify the folder containing the text files
		String folderPath = jarDirectory; // Use the JAR directory as the folder path

		// Call the method to process all files in the folder
		processFilesInFolder(folderPath);
	}

	private static void processFilesInFolder(String folderPath) {
		File folder = new File(folderPath);

		// Check if the provided path is a directory
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isFile() && file.getName().endsWith(".txt")) {
						System.out.println("Processing file: " + file.getName());
						leituraArquivoTexto(file.getAbsolutePath(), file.getName());
					}
				}
			} else {
				System.err.println("No files found in the specified folder.");
			}
		} else {
			System.err.println("Invalid folder path: " + folderPath);
		}
	}

	private static void leituraArquivoTexto(String folderPath, String fileName) {
		String caminhoArquivo = folderPath;
		String resultadoPoco = null;
		String resultadoLongitude = null;
		String resultadoLatitude = null;
		String resultadoBap = null;
		String resultadoMaiorProf = null;
		String resultadoLACHENBRUCH_BREWER = null;
		String resultadoTemperatura = null;
		double maiorTemperatura = Double.MIN_VALUE; // Initialize with the smallest possible value
		boolean encontrouPoco = false;
		boolean encontrouLongitude = false;
		boolean encontrouLatitude = false;
		boolean encontrouBap = false;
		boolean encontrouMaiorProf = false;

		try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
			String linha;

			while ((linha = leitor.readLine()) != null) {

//					if (linha.contains("POCO           :")) {
//						System.out.println(linha + "*****");
//				        encontrouPoco = true;
//				        int indiceInicio = linha.indexOf("POÇO:") + "POÇO:".length();
//				        if (indiceInicio < linha.length()) {
//				            resultadoPoco = linha.substring(indiceInicio).trim();
//				        }
//				    }

				if (linha.contains(" POÇO           :")) {
					encontrouPoco = true;
					// Extrai o "POÇO"
					resultadoPoco = linha.substring(linha.indexOf("POÇO") + "POÇO".length()).trim();
				}

				// Move the encontrouPoco check outside of the if block
				if (encontrouPoco) {
					resultadoPoco = linha; // Salva o texto que vem após "POÇO"
					encontrouPoco = false; // Reseta a flag após encontrar
				}

		        if (linha.contains("LONGITUDE      :")) {
		            encontrouLongitude = true;
		            // Extrai o texto após "LONGITUDE :"
		            resultadoLongitude = linha.substring(linha.indexOf("LONGITUDE      :") + "LONGITUDE      :".length()).trim();
		        }

		        // Move the encontrouLongitude check outside of the if block
		        if (encontrouLongitude) {
		            resultadoLongitude = linha.substring(linha.indexOf("LONGITUDE      :") + "LONGITUDE      :".length()).trim();
		            encontrouLongitude = false; // Reseta a flag após encontrar
		        }
				if (linha.contains("LATITUDE")) {
					encontrouLatitude = true;
				}

				if (encontrouLatitude) {
					resultadoLatitude = linha; // Salva o texto que vem após "LATITUDE"
					encontrouLatitude = false; // Reseta a flag após encontrar
					if (resultadoLatitude.contains(")")) {
						resultadoLatitude = resultadoLatitude.substring(0, resultadoLatitude.indexOf(")"));
					}
				}

				if (linha.contains("B.A.P")) {
					encontrouBap = true;
					// Extrai o texto até antes de "P.F.SONDADOR" após "B.A.P"
					int indexPF = linha.indexOf("P.F.SONDADOR");
					if (indexPF != -1) {
						resultadoBap = linha.substring(linha.indexOf("B.A.P"), indexPF).trim();
					} else {
						// Se não encontrar "P.F.SONDADOR", pega o texto completo após "B.A.P"
						resultadoBap = linha.substring(linha.indexOf("B.A.P")).trim();
					}
				}

				if (encontrouBap) {
					resultadoBap = linha; // Salva o texto que vem após "B.A.P"
					encontrouBap = false; // Reseta a flag após encontrar
				}

				if (linha.contains("MAIOR PROF.")) {
					encontrouMaiorProf = true;
					// Verifica se a linha contém "INICIO"
					if (linha.contains("INICIO")) {
						// Extrai o texto entre "MAIOR PROF." e "INICIO"
						resultadoMaiorProf = linha.substring(linha.indexOf("MAIOR PROF.") + "MAIOR PROF.".length(),
								linha.indexOf("INICIO")).trim();
					} else {
						// Se não contiver "INICIO", extrai o "MAIOR PROF."
						resultadoMaiorProf = linha.substring(linha.indexOf("MAIOR PROF.") + "MAIOR PROF.".length())
								.trim();
					}
				}

				// Check if the line contains "LACHENBRUCH & BREWER"
				if (linha.contains("LACHENBRUCH & BREWER")) {
					// Extract the information after "LACHENBRUCH & BREWER"
					String infoAfterKeyword = linha
							.substring(linha.indexOf("LACHENBRUCH & BREWER") + "LACHENBRUCH & BREWER".length()).trim();

					// Accumulate the extracted information
					resultadoLACHENBRUCH_BREWER += infoAfterKeyword + "\n";
				}

				if (linha.contains("TEMPERATURA FUNDO POCO:")) {
					// Extract the temperature value after "TEMPERATURA FUNDO POCO::"
					String temperaturaTexto = linha
							.substring(linha.indexOf("TEMPERATURA FUNDO POCO:") + "TEMPERATURA FUNDO POCO:".length())
							.trim();

					try {
						double temperatura = Double.parseDouble(temperaturaTexto);

						// Check if the current temperature is greater than the previous maximum
						if (temperatura > maiorTemperatura) {
							maiorTemperatura = temperatura;
						}
					} catch (NumberFormatException e) {
						// Handle the case where the temperature value is not a valid double
						System.err.println("Error parsing temperature value: " + temperaturaTexto);
					}
				}

				if (encontrouPoco) {
					resultadoPoco = linha; // Salva o texto que vem após "POÇO"
					encontrouPoco = false; // Reseta a flag após encontrar
				}

				if (encontrouLatitude) {
					resultadoLatitude = linha; // Salva o texto que vem após "LATITUDE" até o primeiro parêntese
					encontrouLatitude = false; // Reseta a flag após encontrar
					if (resultadoLatitude.contains(")")) {
						resultadoLatitude = resultadoLatitude.substring(0, resultadoLatitude.indexOf(")"));
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		printResultsToFileTXT(resultadoPoco, resultadoLongitude, resultadoLatitude, resultadoBap, resultadoMaiorProf,
				resultadoLACHENBRUCH_BREWER, maiorTemperatura, fileName);

		printResults(resultadoPoco, resultadoLongitude, resultadoLatitude, resultadoBap, resultadoMaiorProf,
				resultadoLACHENBRUCH_BREWER, maiorTemperatura);
	}

	private static void printResults(String resultadoPoco, String resultadoLongitude, String resultadoLatitude,
			String resultadoBap, String resultadoMaiorProf, String resultadoLACHENBRUCH_BREWER,
			double maiorTemperaturaFundoPoco) {
		if (resultadoPoco != null) {
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("-------------------------INICIO---------------------------");
			System.out.println(" " + resultadoPoco);
		} else {
			System.out.println("Palavra 'POÇO' não encontrada no arquivo.");
		}

		if (resultadoLongitude != null) {
			System.out.println("'LONGITUDE      :' " + resultadoLongitude);
		} else {
			System.out.println("Palavra 'LONGITUDE      :' não encontrada no arquivo.");
		}

		if (resultadoLatitude != null) {
			System.out.println(resultadoLatitude);
		} else {
			System.out.println("Palavra 'LATITUDE' não encontrada no arquivo.");
		}

		if (resultadoBap != null) {
			System.out.println(" 'B.A.P': " + resultadoBap);
		} else {
			System.out.println("Palavra 'B.A.P' não encontrada no arquivo.");
		}

		if (resultadoMaiorProf != null) {
			System.out.println(" 'MAIOR PROF.': " + resultadoMaiorProf);
		} else {
			System.out.println("Palavra 'MAIOR PROF.' não encontrada no arquivo.");
		}

		if (maiorTemperaturaFundoPoco != Double.MIN_VALUE) {
			double convertido = converterFahrenheitParaCelsius(maiorTemperaturaFundoPoco);
			System.out.println("Maior valor de 'TEMPERATURA FUNDO POCO: em Fahrenheit' = " + maiorTemperaturaFundoPoco);
			System.out.println("Maior valor de 'TEMPERATURA FUNDO POCO: em Celcius' = " + convertido);

		} else {
			System.out.println("Palavra 'TEMPERATURA FUNDO POCO:' não encontrada no arquivo.");
		}
		if (resultadoLACHENBRUCH_BREWER != null && !resultadoLACHENBRUCH_BREWER.isEmpty()) {
			double convertido = converterFahrenheitParaCelsius(maiorTemperaturaFundoPoco);
			System.out.println(
					"Informações após 'LACHENBRUCH & BREWER' em Fahrenheit': \n" + resultadoLACHENBRUCH_BREWER);
			System.out.println("Informações após 'LACHENBRUCH & BREWER' em Celcius': \n\n" + +convertido);
			// Salvar o valor no arquivo ou realizar outras operações, se necessário
		} else {
			System.out.println("\n Palavra 'LACHENBRUCH & BREWER' não encontrada no arquivo.");
			// Não salvar o valor no arquivo
		}
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("-------------------------FIM---------------------------");

//		printResultsToFileTXT(resultadoPoco, resultadoLongitude, resultadoLatitude, resultadoBap, resultadoMaiorProf,
//				resultadoLACHENBRUCH_BREWER, maiorTemperaturaFundoPoco);

	}

	private static void printResultsToFileTXT(String resultadoPoco, String resultadoLongitude, String resultadoLatitude,
			String resultadoBap, String resultadoMaiorProf, String resultadoLACHENBRUCH_BREWER,
			double maiorTemperaturaFundoPoco, String fileName) {

		try {
			// Verifique se a pasta DadosExtraidos existe; se não, crie-a
			File directory = new File(filePath);
			if (!directory.exists()) {
				directory.mkdir();
				System.out.println("Caminho criado.");
			} else {
				System.out.println("Caminho já existe.");
			}
			// Caminho do arquivo completo, incluindo o nome do arquivo
			String fullFilePath = filePath + "extracted_results.txt";
//			    Path filePath = Paths.get(folderPath);

//		        String fileName = filePath.getFileName().toString(); // Extract the file name from the path

			// Cria o BufferedWriter para o arquivo
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath, true))) {
				writer.write("------------------------------------------------------------------------------\n");
				writer.write("------------------------------------------------------------------------------\n");
				writer.write("-------------------------INICIO DO "+fileName+"---------------------------\n\n");
				writeResultToFile(writer, "NOME DO ARQUIVO:", fileName);
				writeResultToFile(writer, " 'LONGITUDE' :", resultadoLongitude);
				writeResultToFile(writer, "", resultadoLatitude + ")");
				writeResultToFile(writer, " 'B.A.P' :", resultadoBap);
				writeResultToFile(writer, " 'MAIOR PROF. :", resultadoMaiorProf);

				if (maiorTemperaturaFundoPoco != Double.MIN_VALUE) {
					double convertido = converterFahrenheitParaCelsius(maiorTemperaturaFundoPoco);
					writer.write("Maior valor de 'TEMPERATURA FUNDO POCO:': " + maiorTemperaturaFundoPoco
							+ "  Fahrenheit \n");
					writer.write("Maior valor de 'TEMPERATURA FUNDO POCO:': " + convertido + " Celcius \n");

				} else {
					writer.write("Palavra 'TEMPERATURA FUNDO POCO:' não encontrada no arquivo.\n");
				}

				if (resultadoLACHENBRUCH_BREWER != null && !resultadoLACHENBRUCH_BREWER.isEmpty()) {
					writer.write("Informações após 'LACHENBRUCH & BREWER':\n" + resultadoLACHENBRUCH_BREWER);
//			            writer.write("Informações após 'LACHENBRUCH & BREWER':\n" + resultadoLACHENBRUCH_BREWER);
				} else {
					writer.write("Palavra 'LACHENBRUCH & BREWER' não encontrada no arquivo.\n");
				}
				writer.write("-------------------------FIM DO "+fileName+"---------------------------\n");
				writer.write("------------------------------------------------------------------------------\n");
				writer.write("------------------------------------------------------------------------------\n\n\n\n");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static double converterFahrenheitParaCelsius(double temperaturaFahrenheit) {
		return (temperaturaFahrenheit - 32) * 5 / 9;
	}

	private static void writeResultToFile(BufferedWriter writer, String description, String result) throws IOException {
		if (result != null) {
			writer.write(description + " " + result + "\n");
		} else {
			writer.write(description + " não encontrada no arquivo.\n");
		}
	}

//	    private static void printResultsToFile(String resultadoPoco, String resultadoLongitude, String resultadoLatitude,
//	            String resultadoBap, String resultadoMaiorProf, String resultadoLACHENBRUCH_BREWER,
//	            double maiorTemperaturaFundoPoco) {
//
//	        try {
//	            File directory = new File(filePath);
//	            if (!directory.exists()) {
//	                directory.mkdir();
//	                System.out.println("Caminho criado.");
//	            } else {
//	                System.out.println("Caminho já existe.");
//	            }
//
//	            String fullFilePath = filePath + "extracted_results.xlsx";
//
//	            try (Workbook workbook = new XSSFWorkbook();
//	                 FileOutputStream fileOut = new FileOutputStream(fullFilePath)) {
//
//	                Sheet sheet = workbook.createSheet("Resultados");
//
//	                int rowNum = 0;
//	                Row row = sheet.createRow(rowNum++);
//	                writeResultToSheet(row, " 'POÇO':", resultadoPoco);
//	                writeResultToSheet(row, " 'LONGITUDE' :", resultadoLongitude);
//	                writeResultToSheet(row, " 'LATITUDE' :", resultadoLatitude + ")");
//	                writeResultToSheet(row, " 'B.A.P' :", resultadoBap);
//	                writeResultToSheet(row, " 'MAIOR PROF. :", resultadoMaiorProf);
//
//	                if (maiorTemperaturaFundoPoco != Double.MIN_VALUE) {
//	                    double convertido = converterFahrenheitParaCelsius(maiorTemperaturaFundoPoco);
//	                    writeResultToSheet(row, "Maior valor de 'TEMPERATURA FUNDO POCO: em Fahrenheit'", String.valueOf(maiorTemperaturaFundoPoco));
//	                    writeResultToSheet(row, "Maior valor de 'TEMPERATURA FUNDO POCO: em Celsius'", String.valueOf(convertido));
//	                } else {
//	                    writeResultToSheet(row, "Palavra 'TEMPERATURA FUNDO POCO:' não encontrada no arquivo.", null);
//	                }
//
//	                if (resultadoLACHENBRUCH_BREWER != null && !resultadoLACHENBRUCH_BREWER.isEmpty()) {
//	                    writeResultToSheet(row, "Informações após 'LACHENBRUCH & BREWER'", resultadoLACHENBRUCH_BREWER);
//	                } else {
//	                    writeResultToSheet(row, "Palavra 'LACHENBRUCH & BREWER' não encontrada no arquivo.", null);
//	                }
//
//	                workbook.write(fileOut);
//	            }
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    private static void writeResultToSheet(Row row, String description, String result) {z
//	        Cell cell = row.createCell(0);
//	        cell.setCellValue(description);
//
//	        if (result != null) {
//	            cell = row.createCell(1);
//	            cell.setCellValue(result);
//	        } else {
//	            cell = row.createCell(1, CellType.BLANK);
//	        }
}
