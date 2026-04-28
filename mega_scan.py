import os
import re

def scan_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    issues = []

    # Check for hardcoded string values instead of resources
    if 'Log.' in content and 'Log.e' not in content and 'Log.d' not in content:
        issues.append("Suspicious Logging found.")

    # Check for empty catch blocks
    if re.search(r'catch\s*\([^)]+\)\s*{\s*}', content):
        issues.append("Empty catch block (swallowing exceptions silently).")

    # Check for UI blocking thread calls (Thread.sleep)
    if 'Thread.sleep' in content:
        issues.append("Thread.sleep used, potentially blocking UI.")

    # Check for Missing Return Types on Repositories
    if 'suspend fun' in content and 'AppResult' not in content and 'Result' not in content and 'Response' not in content and 'Unit' not in content:
        issues.append("Suspend function might be missing a proper Result wrapper.")

    if issues:
        print(f"Issues in {filepath}:")
        for issue in issues:
            print(f"  - {issue}")

for root, _, files in os.walk('app/src'):
    for file in files:
        if file.endswith('.kt'):
            scan_file(os.path.join(root, file))
