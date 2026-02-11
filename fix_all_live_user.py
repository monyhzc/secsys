#!/usr/bin/env python3

# Fix all live_user INSERT statements in SQL file

with open('database/grad_wygl.sql', 'r') as f:
    lines = f.readlines()

fixed_lines = []
for line in lines:
    if line.strip().startswith('INSERT INTO `live_user` VALUES ('):
        # Count the number of values
        values_part = line.split('VALUES (')[1].split(');')[0]
        values = values_part.split(',')
        
        # If company_id is missing, add it
        if len(values) == 11:
            # Add company_id = 1 at the end
            fixed_values = values + [' 1']
            fixed_values_part = ','.join(fixed_values)
            fixed_line = line.replace(values_part, fixed_values_part)
            fixed_lines.append(fixed_line)
        else:
            fixed_lines.append(line)
    else:
        fixed_lines.append(line)

with open('database/grad_wygl.sql', 'w') as f:
    f.writelines(fixed_lines)

print('Fixed all live_user INSERT statements')
