package com.example.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExtratorDadosPetApplication {

	static String jarDirectory = System.getProperty("user.dir");
	static String userHome = System.getProperty("user.home");
	static String filePath = jarDirectory + File.separator + "DadosExtraidos" + File.separator ;
	static String fileTXTPath = System.getProperty("user.home") + File.separator + "DadosExtraidos" + File.separator + "extracted_results.txt";

	
	public static void main(String[] args) {
	    SpringApplication.run(ExtratorDadosPetApplication.class, args);

	    // Get the current working directory (where the JAR is located)
	    String jarDirectory = System.getProperty("user.dir");

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
                        leituraArquivoTexto(file.getAbsolutePath());
                    }
                }
            } else {
                System.err.println("No files found in the specified folder.");
            }
        } else {
            System.err.println("Invalid folder path: " + folderPath);
        }
    }

	private static void leituraArquivoTexto(String folderPath) {
		String caminhoArquivo = folderPath;
		String resultadoPoco = null;
		String resultadoLongitude = null;
		String resultadoLatitude = null;
		String resultadoBap = null;
		String resultadoMaiorProf = null;
		String resultadoLACHENBRUCH_BREWER = null;
		String resultadoTemperatura = null;
		double maiorTemperatura = Double.MIN_VALUE; // Initialize with the smallest possible value

		try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo))) {
			String linha;
			boolean encontrouPoco = false;
			boolean encontrouLongitude = false;
			boolean encontrouLatitude = false;
			boolean encontrouBap = false;
			boolean encontrouMaiorProf = false;

			while ((linha = leitor.readLine()) != null) {
				if (linha.contains("POCO           :")) {
					System.out.println(linha + "*****");
			        encontrouPoco = true;
			        int indiceInicio = linha.indexOf("POÇO:") + "POÇO:".length();
			        if (indiceInicio < linha.length()) {
			            resultadoPoco = linha.substring(indiceInicio).trim();
			        }
			    }

				if (linha.contains("LONGITUDE      :")) {
					encontrouLongitude = true;
					// Extrai o texto após "LONGITUDE :"
					resultadoLongitude = linha
							.substring(linha.indexOf("LONGITUDE      :") + "LONGITUDE      :".length()).trim();
				}

				if (linha.contains("LATITUDE")) {
					encontrouLatitude = true;
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

				if (linha.contains("MAIOR PROF.")) {
					encontrouMaiorProf = true;
					// Verifica se a linha contém "INICIO"
					if (linha.contains("INICIO")) {
						// Extrai o texto entre "MAIOR PROF." e "INICIO"
						resultadoMaiorProf = linha.substring(linha.indexOf("MAIOR PROF.") + "MAIOR PROF.".length(),
								linha.indexOf("INICIO")).trim();
					} else {
						// Se não contiver "INICIO", extrai o texto após "MAIOR PROF."
						resultadoMaiorProf = linha.substring(linha.indexOf("MAIOR PROF.") + "MAIOR PROF.".length())
								.trim();
					}
				}
				            // Check if the line contains "LACHENBRUCH & BREWER"
				            if (linha.contains("LACHENBRUCH & BREWER")) {
				                // Extract the information after "LACHENBRUCH & BREWER"
				                String infoAfterKeyword = linha.substring(linha.indexOf("LACHENBRUCH & BREWER") + "LACHENBRUCH & BREWER".length()).trim();

				                // Accumulate the extracted information
				                resultadoLACHENBRUCH_BREWER += infoAfterKeyword + "\n";
				            }
					
					if (linha.contains("TEMPERATURA FUNDO POCO:")) {
						// Extract the temperature value after "TEMPERATURA FUNDO POCO:"
						String temperaturaTexto = linha
								.substring(
										linha.indexOf("TEMPERATURA FUNDO POCO:") + "TEMPERATURA FUNDO POCO:".length())
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

		printResults(resultadoPoco, resultadoLongitude, resultadoLatitude, resultadoBap, resultadoMaiorProf,
				resultadoLACHENBRUCH_BREWER, maiorTemperatura);
	}

	private static void printResults(String resultadoPoco, String resultadoLongitude, String resultadoLatitude,
			String resultadoBap, String resultadoMaiorProf, String resultadoLACHENBRUCH_BREWER,
			double maiorTemperatura) {
		if (resultadoPoco != null) {
			System.out.println("Texto após " + resultadoPoco);
		} else {
			System.out.println("Palavra 'POÇO' não encontrada no arquivo.");
		}

		if (resultadoLongitude != null) {
			System.out.println("Texto após 'LONGITUDE      :' " + resultadoLongitude);
		} else {
			System.out.println("Palavra 'LONGITUDE      :' não encontrada no arquivo.");
		}

		if (resultadoLatitude != null) {
			System.out.println("Texto após 'LATITUDE': " + resultadoLatitude);
		} else {
			System.out.println("Palavra 'LATITUDE' não encontrada no arquivo.");
		}

		if (resultadoBap != null) {
			System.out.println("Texto após 'B.A.P': " + resultadoBap);
		} else {
			System.out.println("Palavra 'B.A.P' não encontrada no arquivo.");
		}

		if (resultadoMaiorProf != null) {
			System.out.println("Texto após 'MAIOR PROF.': " + resultadoMaiorProf);
		} else {
			System.out.println("Palavra 'MAIOR PROF.' não encontrada no arquivo.");
		}

		if (maiorTemperatura != Double.MIN_VALUE ) {
			System.out.println("Maior valor de 'TEMPERATURA FUNDO POCO': " + maiorTemperatura);
		} else {
			System.out.println("Palavra 'TEMPERATURA FUNDO POCO' não encontrada no arquivo.");
		}
		if ( resultadoLACHENBRUCH_BREWER != null && !resultadoLACHENBRUCH_BREWER.isEmpty()) {
		    System.out.println("Informações após 'LACHENBRUCH & BREWER':\n" + resultadoLACHENBRUCH_BREWER);
		    // Salvar o valor no arquivo ou realizar outras operações, se necessário
		} else {
		    System.out.println("Palavra 'LACHENBRUCH & BREWER' não encontrada no arquivo.");
		    // Não salvar o valor no arquivo
		}



		printResultsToFile(resultadoPoco, resultadoLongitude, resultadoLatitude, resultadoBap, resultadoMaiorProf,
                resultadoLACHENBRUCH_BREWER, maiorTemperatura);
	}
	
	private static void printResultsToFile(String resultadoPoco, String resultadoLongitude, String resultadoLatitude,
	        String resultadoBap, String resultadoMaiorProf, String resultadoLACHENBRUCH_BREWER,
	        double maiorTemperatura) {

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

		    // Cria o BufferedWriter para o arquivo
		    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath, true))) {
		        writer.write("--------------------------\n");
		        writer.write("--------------------------\n");
		        writer.write("--------------------------\n");
		        writeResultToFile(writer, "Texto após 'POÇO':", resultadoPoco);
		        writeResultToFile(writer, "Texto após 'LONGITUDE' :", resultadoLongitude);
		        writeResultToFile(writer, "Texto após 'LATITUDE' :", resultadoLatitude + ")");
		        writeResultToFile(writer, "Texto após 'B.A.P' :", resultadoBap);
		        writeResultToFile(writer, "Texto após 'MAIOR PROF. :", resultadoMaiorProf);

		        if (maiorTemperatura != Double.MIN_VALUE) {
		            writer.write("Maior valor de 'TEMPERATURA FUNDO POCO': " + maiorTemperatura + "\n");
		        } else {
		            writer.write("Palavra 'TEMPERATURA FUNDO POCO' não encontrada no arquivo.\n");
		        }

		        if (resultadoLACHENBRUCH_BREWER != null &&  !resultadoLACHENBRUCH_BREWER.isEmpty() ) {
		            writer.write("Informações após 'LACHENBRUCH & BREWER':\n" + resultadoLACHENBRUCH_BREWER);
		        } else {
		            writer.write("Palavra 'LACHENBRUCH & BREWER' não encontrada no arquivo.\n");
		        }
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

    private static void writeResultToFile(BufferedWriter writer, String description, String result) throws IOException {
        if (result != null) {
            writer.write(description + " " + result + "\n");
        } else {
            writer.write(description + " não encontrada no arquivo.\n");
        }
    }

}
