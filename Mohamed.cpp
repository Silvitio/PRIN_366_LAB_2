#include "FractionalArithmeticCalculator.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <stdexcept>
#include <iomanip>
#include <algorithm>

using namespace std;

template <typename S>
ostream& operator<<(ostream& os, const vector<S>& vector) {
    for (auto i : vector)
        os << i << " ";
    return os;
}

void FractionalArithmeticCalculator::run(const string& inputFilePath, const string& outputFilePath) {
    cout << "DEBUG: Running calculator with input file: " << inputFilePath << " and output file: " << outputFilePath << endl;
    try {
        readInputFile(inputFilePath);
        validateInput();
        string result = performOperation();
        writeOutputFile(outputFilePath, result);
    }
    catch (const exception& e) {
        writeOutputFile(outputFilePath, e.what());
        cerr << "DEBUG: Exception caught in run: " << e.what() << endl;
        throw;
    }
}

void FractionalArithmeticCalculator::readInputFile(const string& inputFilePath) {
    cout << "DEBUG: Reading input file: " << inputFilePath << endl;
    ifstream inputFile(inputFilePath);
    if (!inputFile) {
        throw runtime_error("Invalid input file path");
    }
    inputFile >> firstNumber >> operation >> secondNumber;
    inputFile.close();
    cout << "DEBUG: Read firstNumber: " << firstNumber << ", operation: " << operation << ", secondNumber: " << secondNumber << endl;
}

void FractionalArithmeticCalculator::writeOutputFile(const string& outputFilePath, const string& result) {
    cout << "DEBUG: Writing result to output file: " << outputFilePath << endl;
    ofstream outputFile(outputFilePath);
    if (!outputFile) {
        throw runtime_error("Invalid output file path");
    }
    outputFile << result;
    outputFile.close();
    cout << "DEBUG: Result written: " << result << endl;
}

void FractionalArithmeticCalculator::validateInput() {
    cout << "DEBUG: Validating input..." << endl;
    if (areInputsEmpty()) {
        throw runtime_error("Invalid number input");
    }
    if (!isOperationValid(operation)) {
        throw runtime_error("Invalid operation type");
    }
    if (operation == "/" && secondNumber == "0") {
        throw runtime_error("Division by zero error");
    }
    validateNumber(firstNumber);
    validateNumber(secondNumber);
    cout << "DEBUG: Input validation successful." << endl;
}

void FractionalArithmeticCalculator::validateNumber(const string& number) {
    cout << "DEBUG: Validating number: " << number << endl;
    size_t dotPos = number.find('.');
    string wholePart = number.substr(0, dotPos);
    string fractionalPart = (dotPos != string::npos) ? number.substr(dotPos + 1) : "";

    if (wholePart.length() > 15 || fractionalPart.length() > 15) {
        throw runtime_error("Number exceeds 15 digits before or after decimal point");
    }

    if (!all_of(wholePart.begin(), wholePart.end(), [](char c) { return isdigit(c) || c == '-'; }) ||
        !all_of(fractionalPart.begin(), fractionalPart.end(), [](char c) { return isdigit(c); })) {
        throw runtime_error("Invalid character in number");
    }
}

bool FractionalArithmeticCalculator::isOperationValid(const string& operation) const {
    cout << "DEBUG: Checking if operation is valid: " << operation << endl;
    return operation == "+" || operation == "-" || operation == "*" || operation == "/";
}

bool FractionalArithmeticCalculator::areInputsEmpty() const {
    cout << "DEBUG: Checking if inputs are empty..." << endl;
    return firstNumber.empty() || secondNumber.empty() || operation.empty();
}

string FractionalArithmeticCalculator::performOperation() {
    cout << "DEBUG: Performing operation: " << firstNumber << " " << operation << " " << secondNumber << endl;

    int fracLen1 = 0, fracLen2 = 0;
    vector<int> num1 = stringToVector(firstNumber, fracLen1);
    vector<int> num2 = stringToVector(secondNumber, fracLen2);

    // Align fractional parts
    if (fracLen1 > fracLen2) {
        num2.insert(num2.end(), fracLen1 - fracLen2, 0);
    }
    else {
        num1.insert(num1.end(), fracLen2 - fracLen1, 0);
    }

    vector<int> result;
    int fractionalLength = 0;
    bool isNegative = false; // Track if result should be negative

    // Handle addition
    if (operation == "+") {
        result = add(num1, num2);
        fractionalLength = max(fracLen1, fracLen2);  // Adjust fractional length after addition
    }
    // Handle subtraction
    else if (operation == "-") {
        result = subtract(num1, num2, isNegative);
        fractionalLength = max(fracLen1, fracLen2);  // Adjust fractional length after subtraction
    }
    // Handle multiplication
    else if (operation == "*") {
        isNegative = (firstNumber[0] == '-' && secondNumber[0] != '-') ||
            (firstNumber[0] != '-' && secondNumber[0] == '-');
        result = multiply(num1, num2, fracLen1, fracLen2);
        fractionalLength = fracLen1 + fracLen2;
    }
    // Handle division
    else if (operation == "/") {
        if (secondNumber == "0") {
            throw runtime_error("Division by zero error");
        }
        isNegative = (firstNumber[0] == '-' && secondNumber[0] != '-') ||
            (firstNumber[0] != '-' && secondNumber[0] == '-');
        result = longDivision(num1, num2, fractionalLength);
    }

    // Convert the result back to a string
    std::string resultStr = vectorToString(result, fractionalLength, isNegative);  // Pass isNegative to handle negative output
    cout << "DEBUG: Final result: " << resultStr << endl;

    return resultStr;
}

vector<int> FractionalArithmeticCalculator::add(const vector<int>& num1, const vector<int>& num2) {
    cout << "DEBUG: Adding numbers..." << endl;

    vector<int> result;
    int carry = 0;
    int maxSize = max(num1.size(), num2.size());

    vector<int> num1Rev = num1;
    vector<int> num2Rev = num2;
    reverse(num1Rev.begin(), num1Rev.end());
    reverse(num2Rev.begin(), num2Rev.end());

    for (int i = 0; i < maxSize || carry; i++) {
        int digit1 = (i < num1Rev.size()) ? num1Rev[i] : 0;
        int digit2 = (i < num2Rev.size()) ? num2Rev[i] : 0;
        int sum = digit1 + digit2 + carry;
        result.push_back(sum % 10);
        carry = sum / 10;
    }

    reverse(result.begin(), result.end());

    // Handle carry that might add extra precision or alignment
    while (result.size() > 1 && result.front() == 0) {
        result.erase(result.begin());
    }

    cout << "DEBUG: Addition result: ";
    for (int digit : result) {
        cout << digit;
    }
    cout << endl;

    return result;
}

vector<int> FractionalArithmeticCalculator::subtract(const vector<int>& num1, const vector<int>& num2, bool& isNegative) {
    cout << "DEBUG: Subtracting numbers..." << endl;

    vector<int> result;

    vector<int> larger = num1, smaller = num2;

    if (num1.size() < num2.size() || (num1.size() == num2.size() && num1 < num2)) {
        larger = num2;
        smaller = num1;
        isNegative = true;  // Mark result as negative if num2 is larger
    }
    else {
        isNegative = false; // Otherwise, result is positive
    }

    reverse(larger.begin(), larger.end());
    reverse(smaller.begin(), smaller.end());

    int carry = 0;
    for (size_t i = 0; i < larger.size(); i++) {
        int digit1 = larger[i];
        int digit2 = (i < smaller.size()) ? smaller[i] : 0;
        int diff = digit1 - digit2 - carry;

        if (diff < 0) {
            diff += 10;
            carry = 1;
        }
        else {
            carry = 0;
        }

        result.push_back(diff);
    }

    // Remove leading zeros after subtraction
    while (result.size() > 1 && result.back() == 0) {
        result.pop_back();
    }

    reverse(result.begin(), result.end());

    cout << "DEBUG: Subtraction result: ";
    for (int digit : result) {
        cout << digit;
    }
    cout << endl;

    return result;
}

vector<int> FractionalArithmeticCalculator::multiply(const vector<int>& num1, const vector<int>& num2, int fracLen1, int fracLen2) {
    cout << "DEBUG: Multiplying numbers..." << endl;

    vector<int> result(num1.size() + num2.size(), 0);

    for (int i = num1.size() - 1; i >= 0; i--) {
        for (int j = num2.size() - 1; j >= 0; j--) {
            int product = num1[i] * num2[j] + result[i + j + 1];
            result[i + j + 1] = product % 10;
            result[i + j] += product / 10;
        }
    }

    // Remove leading zeros after multiplication
    while (result.size() > 1 && result[0] == 0) {
        result.erase(result.begin());
    }

    cout << "DEBUG: Multiplication result: ";
    for (int digit : result) {
        cout << digit;
    }
    cout << endl;

    return result;
}

vector<int> FractionalArithmeticCalculator::longDivision(const vector<int>& num1, const vector<int>& num2, int& fractionalLength) {
    cout << "DEBUG: Performing long division..." << endl;
    vector<int> result;
    vector<int> current;
    int precisionCounter = 0;
    const int maxPrecision = MAX_PRECISION;  // Maximum fractional precision allowed

    for (size_t i = 0; i < num1.size() || (precisionCounter < maxPrecision && !current.empty()); ++i) {
        if (i < num1.size()) {
            current.push_back(num1[i]); // Continue processing the integer part
        }
        else {
            // Add zeros after processing all integer digits for fractional part
            current.push_back(0);
            precisionCounter++;
        }
        // Remove leading zeros from the current value
        removeLeadingZeros(current);
        // If current cannot be divided by num2, add a zero to the result and continue
        if (!canDivide(current, num2)) {
            result.push_back(0);
            continue;
        }
        // Perform division by subtracting num2 from current repeatedly
        result.push_back(performDivisionStep(current, num2));

        // Stop if we reached the maximum precision after the decimal
        if (precisionCounter >= maxPrecision) {
            break;
        }
    }

    fractionalLength = precisionCounter;
    cout << "DEBUG: Long division result: " << result << endl;
    return result;
}

vector<int> FractionalArithmeticCalculator::stringToVector(const string& numStr, int& fractionalLength) {
    cout << "DEBUG: Converting string to vector: " << numStr << endl;

    size_t decimalPos = numStr.find('.');
    string intPart = decimalPos == string::npos ? numStr : numStr.substr(0, decimalPos);
    string fracPart = decimalPos == string::npos ? "" : numStr.substr(decimalPos + 1);

    fractionalLength = fracPart.length();
    string combined = intPart + fracPart;

    vector<int> result;
    for (char ch : combined) {
        if (isdigit(ch)) {
            result.push_back(ch - '0');
        }
    }

    cout << "DEBUG: Vector conversion result: ";
    for (int digit : result) {
        cout << digit;
    }
    cout << endl;

    return result;
}

string FractionalArithmeticCalculator::vectorToString(const vector<int>& digits, int fractionalLength, bool isNegative) const {
    cout << "DEBUG: Converting vector to string with fractional length: " << fractionalLength << endl;

    string result;
    for (int digit : digits) {
        result += to_string(digit);  // Convert each digit to string
    }

    // Handle fractional part
    if (fractionalLength > 0) {
        if (result.length() > fractionalLength) {
            result.insert(result.end() - fractionalLength, '.');  // Insert decimal point
        }
        else {
            result = "0." + string(fractionalLength - result.length(), '0') + result;  // Handle case like "0.000123"
        }
    }

    // Add negative sign if needed
    if (isNegative) {
        result.insert(result.begin(), '-');
    }

    // Remove leading zeros, while preserving "0.x" format
    while (result.size() > 1 && result[0] == '0' && result[1] != '.') {
        result.erase(0, 1);
    }

    cout << "DEBUG: Final converted string: " << result << endl;
    return result;
}

// Remove leading zeroes in a number
void FractionalArithmeticCalculator::removeLeadingZeros(vector<int>& num) {
    while (num.size() > 1 && num[0] == 0) {
        num.erase(num.begin());
    }
}

// Check if num1 can be divided by num2
bool FractionalArithmeticCalculator::canDivide(const vector<int>& num1, const vector<int>& num2) {
    return num1.size() > num2.size() || (num1.size() == num2.size() && num1 >= num2);
}

// Perform division by subtracting num2 from num1 repeatedly
int FractionalArithmeticCalculator::performDivisionStep(vector<int>& num1, const vector<int>& num2) {
    int quotient = 0;
    bool tempNegativeFlag = false;  // Temporary flag for subtract method
    while (canDivide(num1, num2)) {
        num1 = subtract(num1, num2, tempNegativeFlag);
        quotient++;
    }
    return quotient;
}

int main(int argc, char* argv[]) {
    cout << "DEBUG: Starting program..." << endl;

    if (argc != 3) {
        cerr << "Usage: " << argv[0] << " <input file> <output file>" << endl;
        return 1;
    }

    cout << "DEBUG: Input file: " << argv[1] << endl;
    cout << "DEBUG: Output file: " << argv[2] << endl;

    FractionalArithmeticCalculator calculator;
    try {
        calculator.run(argv[1], argv[2]);
        cout << "DEBUG: Arithmetic operation completed successfully." << endl;
    }
    catch (const exception& e) {
        cerr << "DEBUG: Error occurred: " << e.what() << endl;
        return 1;
    }

    return 0;
}
