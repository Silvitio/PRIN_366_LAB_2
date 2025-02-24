import os

with open("../loqiDomainGenerator/loqiDomainGenerator.py") as f:
    exec(f.read())

# Full map for dependency type lookup
dependency_map = {
    "root": ("root", "none"),
    "nsubj": ("nsubj", "none"),
    "obj": ("obj", "none"),
    "iobj": ("iobj", "none"),
    "nummod": ("nummod", "none"),
    "ccomp": ("ccomp", "none"),
    "xcomp": ("xcomp", "none"),
    "acl": ("acl", "none"),
    "advmod": ("advmod", "none"),
    "advmod:mnr": ("advmod", "manner"),
    "advmod:dgr": ("advmod", "degree"),
    "advmod:plc": ("advmod", "place"),
    "advmod:time": ("advmod", "time"),
    "advmod:idf": ("advmod", "indefinite_frequency"),
    "advmod:df": ("advmod", "definite_frequency"),
    "amod": ("amod", "none"),
    "amod:opn": ("amod", "opinion"),
    "amod:size": ("amod", "size"),
    "amod:pq": ("amod", "physical_quality"),
    "amod:shp": ("amod", "shape"),
    "amod:age": ("amod", "age"),
    "amod:clr": ("amod", "colour"),
    "amod:orgn": ("amod", "origin"),
    "amod:mtrl": ("amod", "material"),
    "amod:type": ("amod", "types"),
    "amod:prps": ("amod", "purpose"),
    "aux": ("aux", "none"),
    "cop": ("cop", "none"),
    "mark": ("mark", "none"),
    "det": ("det", "none"),
    "det:predet": ("det", "predet"),
    "cc": ("cc", "none"),
    "compound": ("compound", "none"),
}


# Function to process a single dependency line
def process_tree_line(line):
    parts = line.strip().split('\t')
    if len(parts) < 4:
        return None

    word_id, word, head_id, dep_type = parts[0], parts[1], parts[2], parts[3]

    if head_id == "0":
        return f"""
obj treeword_{word_id} : TreeWord {{
    word = "{word}" ;
}}
"""

    dep_base, dep_sub = dependency_map.get(dep_type, (dep_type, "none"))

    return f"""
obj treeword_{word_id} : TreeWord {{
    word = "{word}" ;
    isChild(treeword_{head_id}) ;
    isDependant({dep_base}_{dep_sub}, treeword_{head_id}) ;
}}
"""


# Function to process input text and generate TTL format
def process_text(input_text):
    lines = input_text.strip().split('\n')
    return "".join(filter(None, (process_tree_line(line) for line in lines)))


# Append structured sentence format to TTL output
def generate_sentence_structure(sentence):
    word_list = sentence.strip().split()
    sentence_structure = ""
    for i, word in enumerate(word_list):
        word_id = i + 1
        sentence_structure += f"""
obj sentenceword_{word_id} : SentenceWord {{
    word = "{word}" ;
"""
        if i > 0:
            sentence_structure += f'    directlyToTheLeft(sentenceword_{i}) ;\n'
        sentence_structure += "}\n"
    return sentence_structure


def merge_files(file1_path, file2_path, output_file_path):
    """
    Merges the content of two files into a new output file.

    Parameters:
    - file1_path: Path to the first input file.
    - file2_path: Path to the second input file.
    - output_file_path: Path to the output file where the merged content will be written.
    """
    try:
        with open(file1_path, 'r') as file1, open(file2_path, 'r') as file2:
            # Read content from both files
            file1_content = file1.read()
            file2_content = file2.read()

        # Open the output file in write mode
        with open(output_file_path, 'w') as output_file:
            # Write content from both files to the output file
            output_file.write(file1_content)
            output_file.write("\n")  # Optional: Add a newline between the contents
            output_file.write(file2_content)

        print(f"Files merged successfully into {output_file_path}")

    except FileNotFoundError as e:
        print(f"Error: {e}")
    except Exception as e:
        print(f"An error occurred: {e}")


output_file = "examples.loqi"

# Define variables for replacement at the bottom
input_text = """1\the\t3\tnsubj
2\tquickly\t3\tadvmod:mnr
3\treads\t0\troot"""
sentence = "reads he"

# Generate loqi_tree_output and append sentence structure
loqi_tree_output = f"""
// Task
"""
loqi_tree_output += process_text(input_text)
loqi_tree_output += "\n\n" + generate_sentence_structure(sentence)

# Write final output to file
with open(output_file, 'w') as file:
    file.write(loqi_tree_output)
print(f"Final output written to {output_file}")

i = 2

output_file = f'D:/Uni/Trees/Exported/{i}.loqi'

with open(output_file, 'w') as file:
    file.write(loqi_tree_output)
print(f"Final output written to {output_file}")