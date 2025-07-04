#!/usr/bin/env python3
import os
import re

def fix_enum_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Remove lombok imports and annotations
    content = re.sub(r'import lombok\.AllArgsConstructor;\s*', '', content)
    content = re.sub(r'import lombok\.Getter;\s*', '', content)
    content = re.sub(r'@Getter\s*', '', content)
    content = re.sub(r'@AllArgsConstructor\s*', '', content)
    
    # Find enum name
    enum_match = re.search(r'public enum (\w+)', content)
    if not enum_match:
        return
    
    enum_name = enum_match.group(1)
    
    # Find private fields
    field_matches = re.findall(r'private\s+(\w+)\s+(\w+);', content)
    if not field_matches:
        return
    
    # Generate constructor
    constructor_params = ', '.join([f'{field_type} {field_name}' for field_type, field_name in field_matches])
    constructor_assignments = '\n        '.join([f'this.{field_name} = {field_name};' for field_type, field_name in field_matches])
    
    constructor = f"""
    {enum_name}({constructor_params}) {{
        {constructor_assignments}
    }}"""
    
    # Generate getters
    getters = []
    for field_type, field_name in field_matches:
        getter_name = 'get' + field_name[0].upper() + field_name[1:]
        getter = f"""
    public {field_type} {getter_name}() {{
        return {field_name};
    }}"""
        getters.append(getter)
    
    # Insert constructor and getters after private fields
    last_field_pattern = r'(private\s+\w+\s+\w+;)'
    matches = list(re.finditer(last_field_pattern, content))
    if matches:
        last_match = matches[-1]
        insert_pos = last_match.end()
        new_content = content[:insert_pos] + constructor + ''.join(getters) + content[insert_pos:]
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed {file_path}")

# Fix all enum files
enum_dir = "vpp-common/src/main/java/com/example/vvpcommom/Enum"
for file_name in os.listdir(enum_dir):
    if file_name.endswith('.java'):
        file_path = os.path.join(enum_dir, file_name)
        fix_enum_file(file_path) 