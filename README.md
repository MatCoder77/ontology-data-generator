# Instrukcja obsługi

### Wymagane zależności
#### Java SDK 15
Do pobrania ze strony https://openjdk.java.net lub https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html.
#### PDGF 2.7
Należy skontaktować się z firmą bankmark UG poprzez adress info@bankmark.de i poprosić o przyznanie licencji akademickiej. Więcej informacji o narzędzi i sposobie pozyskania licencji na stronie https://www.bankmark.de/products-and-services/pdgf/.

### Sposób uruchomienia
#### Generacja danych
1. Przygotować plik konfiguracyjny i umieścić go w lokalizacji `path_to_configuration_file`.
2. Do folderu wskazanego w konfiguracji jako `input` należy wrzucić plik `input_ontology_file`, zawierający wejściową onotlogię.
3. Wykonać komendę `java -jar ontology-data-generator.jar <input_ontology_file> --spring.config.location=<path_to_configuration_file>`
4. Wynikowa ontologia znajdzie się w folderze wskazanym jako `output` w zadanej konfiguracji.

W celu wyznaczenia metryk ontologicznych z użyciem narzędzia `MetricsCalculator` należy wywołać program z dodatkowym argumentem `calculate-metrics`.
Wtedy komenda przyjmuje postać `java -jar ontology-data-generator.jar <input_ontology_file> --spring.config.location=<path_to_configuration_file> calculate-metrics`
####