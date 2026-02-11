#!/usr/bin/env python3

# Fix SQL file for live_user table

with open('database/grad_wygl.sql', 'r') as f:
    lines = f.readlines()

# Fix lines 661-663
fixed_lines = []
for i, line in enumerate(lines):
    # Fix line 661 (index 660)
    if i == 660:
        # This line is correct
        fixed_lines.append(line)
    # Fix line 662 (index 661)
    elif i == 661:
        if 'VALUES (10' in line and '1, 1, 1, 1);' in line:
            new_line = line.replace('1, 1, 1, 1);', '1, 1, 1, 1, 1);')
            fixed_lines.append(new_line)
        else:
            fixed_lines.append(line)
    # Fix line 663 (index 662)
    elif i == 662:
        if 'VALUES (11' in line and '1, 1, 1, 1);' in line:
            new_line = line.replace('1, 1, 1, 1);', '1, 1, 1, 1, 1);')
            fixed_lines.append(new_line)
        else:
            fixed_lines.append(line)
    # Fix line 664 (index 663)
    elif i == 663:
        if 'VALUES (12' in line and '1, 1, 1);' in line:
            new_line = line.replace('1, 1, 1);', '1, 1, 1, 1, 1);')
            fixed_lines.append(new_line)
        else:
            fixed_lines.append(line)
    else:
        fixed_lines.append(line)

with open('database/grad_wygl.sql', 'w') as f:
    f.writelines(fixed_lines)

print('Fixed SQL file')
