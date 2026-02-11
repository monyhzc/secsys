#!/usr/bin/env python3

with open('database/grad_wygl.sql', 'r') as f:
    lines = f.readlines()

# Find all INSERT statements before their respective CREATE TABLE statements
insert_statements = []
create_statements = {}

# First pass: collect all INSERT and CREATE TABLE statements
for i, line in enumerate(lines):
    if line.strip().startswith('INSERT INTO `'):
        table_name = line.split('`')[1]
        insert_statements.append((i, table_name, line))
    elif line.strip().startswith('CREATE TABLE `'):
        table_name = line.split('`')[1]
        create_statements[table_name] = i

# Find INSERT statements that are before their CREATE TABLE statements
problematic_inserts = []
for insert_line, table_name, insert_sql in insert_statements:
    if table_name in create_statements and insert_line < create_statements[table_name]:
        problematic_inserts.append((insert_line, table_name, insert_sql))

print(f'Found {len(problematic_inserts)} problematic INSERT statements:')
for line_num, table, sql in problematic_inserts[:10]:  # Show first 10
    print(f'Line {line_num + 1}: INSERT INTO `{table}` before CREATE TABLE')

if len(problematic_inserts) > 10:
    print('... and more')
