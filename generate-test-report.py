#!/usr/bin/env python3
"""
Generate comprehensive HTML test report from Maven Surefire XML results
"""

import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from datetime import datetime

def parse_test_results(surefire_dir):
    """Parse all Surefire XML test reports"""
    results = []
    total_tests = 0
    total_failures = 0
    total_errors = 0
    total_skipped = 0
    total_time = 0
    
    xml_files = list(Path(surefire_dir).glob("TEST-*.xml"))
    
    for xml_file in xml_files:
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()
            
            test_class = root.get('name', '').split('.')[-1]
            tests = int(root.get('tests', 0))
            failures = int(root.get('failures', 0))
            errors = int(root.get('errors', 0))
            skipped = int(root.get('skipped', 0))
            time = float(root.get('time', 0))
            
            # Extract test case details
            test_cases = []
            for testcase in root.findall('.//testcase'):
                name = testcase.get('name', 'Unknown')
                time_taken = float(testcase.get('time', 0))
                status = 'passed'
                
                # Check for failures or errors
                if testcase.find('failure') is not None:
                    status = 'failed'
                elif testcase.find('error') is not None:
                    status = 'error'
                elif testcase.find('skipped') is not None:
                    status = 'skipped'
                
                test_cases.append({
                    'name': name,
                    'time': time_taken,
                    'status': status
                })
            
            results.append({
                'class': test_class,
                'tests': tests,
                'failures': failures,
                'errors': errors,
                'skipped': skipped,
                'passed': tests - failures - errors - skipped,
                'time': time,
                'test_cases': test_cases
            })
            
            total_tests += tests
            total_failures += failures
            total_errors += errors
            total_skipped += skipped
            total_time += time
            
        except Exception as e:
            print(f"Warning: Could not parse {xml_file}: {e}", file=sys.stderr)
    
    return {
        'test_classes': results,
        'summary': {
            'total_tests': total_tests,
            'total_passed': total_tests - total_failures - total_errors - total_skipped,
            'total_failures': total_failures,
            'total_errors': total_errors,
            'total_skipped': total_skipped,
            'total_time': total_time,
            'pass_rate': (total_tests - total_failures - total_errors - total_skipped) / total_tests * 100 if total_tests > 0 else 0
        }
    }

def generate_html_report(results, output_file):
    """Generate HTML report"""
    summary = results['summary']
    test_classes = results['test_classes']
    
    # Calculate color based on pass rate
    if summary['pass_rate'] >= 90:
        status_color = "#10b981"  # Green
        status_text = "Excellent"
        status_class = "success"
    elif summary['pass_rate'] >= 70:
        status_color = "#f59e0b"  # Orange
        status_text = "Good"
        status_class = "warning"
    else:
        status_color = "#ef4444"  # Red
        status_text = "Needs Improvement"
        status_class = "error"
    
    # Generate HTML
    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring Boot Playground - Test Report</title>
    <style>
        * {{
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }}
        
        body {{
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }}
        
        .container {{
            max-width: 1400px;
            margin: 0 auto;
        }}
        
        .header {{
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
        }}
        
        .header h1 {{
            color: #333;
            font-size: 2.5em;
            margin-bottom: 10px;
        }}
        
        .header .subtitle {{
            color: #666;
            font-size: 1.1em;
            margin-bottom: 20px;
        }}
        
        .status-badge {{
            display: inline-block;
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: bold;
            color: white;
            background: {status_color};
        }}
        
        .summary-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        
        .summary-card {{
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            text-align: center;
        }}
        
        .summary-card.total {{ border-top: 4px solid #3b82f6; }}
        .summary-card.passed {{ border-top: 4px solid #10b981; }}
        .summary-card.failed {{ border-top: 4px solid #ef4444; }}
        .summary-card.errors {{ border-top: 4px solid #f59e0b; }}
        .summary-card.skipped {{ border-top: 4px solid #6b7280; }}
        .summary-card.time {{ border-top: 4px solid #8b5cf6; }}
        .summary-card.rate {{ border-top: 4px solid {status_color}; }}
        
        .summary-card h3 {{
            color: #666;
            font-size: 0.85em;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 10px;
        }}
        
        .summary-card .value {{
            font-size: 2.5em;
            font-weight: bold;
            color: #333;
        }}
        
        .progress-section {{
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }}
        
        .progress-section h2 {{
            color: #333;
            margin-bottom: 20px;
        }}
        
        .progress-bar-container {{
            background: #e5e7eb;
            border-radius: 10px;
            height: 40px;
            overflow: hidden;
            margin-bottom: 15px;
        }}
        
        .progress-bar {{
            height: 100%;
            background: linear-gradient(90deg, {status_color}, {status_color}dd);
            border-radius: 10px;
            transition: width 0.5s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            font-size: 1.2em;
            width: {summary['pass_rate']:.1f}%;
        }}
        
        .progress-stats {{
            display: flex;
            justify-content: space-between;
            color: #666;
            font-size: 0.95em;
        }}
        
        .test-classes-section {{
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
        }}
        
        .test-classes-section h2 {{
            color: #333;
            margin-bottom: 20px;
        }}
        
        .test-class-item {{
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            border-left: 4px solid #10b981;
            background: #f9fafb;
        }}
        
        .test-class-item.failed {{
            border-left-color: #ef4444;
            background: #fef2f2;
        }}
        
        .test-class-item.has-errors {{
            border-left-color: #f59e0b;
            background: #fffbeb;
        }}
        
        .test-class-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }}
        
        .test-class-name {{
            font-weight: bold;
            color: #333;
            font-size: 1.1em;
        }}
        
        .test-class-stats {{
            display: flex;
            gap: 15px;
            font-size: 0.9em;
        }}
        
        .stat {{
            padding: 4px 8px;
            border-radius: 4px;
            font-weight: bold;
        }}
        
        .stat.passed {{ background: #d1fae5; color: #065f46; }}
        .stat.failed {{ background: #fee2e2; color: #991b1b; }}
        .stat.errors {{ background: #fef3c7; color: #92400e; }}
        .stat.skipped {{ background: #f3f4f6; color: #4b5563; }}
        
        .test-class-time {{
            color: #6b7280;
            font-size: 0.85em;
            margin-top: 5px;
        }}
        
        .footer {{
            text-align: center;
            margin-top: 40px;
            color: rgba(255,255,255,0.8);
            font-size: 0.9em;
        }}
        
        .timestamp {{
            text-align: center;
            color: #666;
            margin-top: 20px;
            font-size: 0.9em;
            padding: 20px;
        }}
        
        .no-tests {{
            text-align: center;
            padding: 40px;
            color: #666;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🧪 Test Report</h1>
            <div class="subtitle">Spring Boot Playground - Comprehensive Unit Testing</div>
            <span class="status-badge">{status_text}</span>
        </div>
        
        <div class="summary-grid">
            <div class="summary-card total">
                <h3>Total Tests</h3>
                <div class="value">{summary['total_tests']}</div>
            </div>
            <div class="summary-card passed">
                <h3>Passed</h3>
                <div class="value">{summary['total_passed']}</div>
            </div>
            <div class="summary-card failed">
                <h3>Failed</h3>
                <div class="value">{summary['total_failures']}</div>
            </div>
            <div class="summary-card errors">
                <h3>Errors</h3>
                <div class="value">{summary['total_errors']}</div>
            </div>
            <div class="summary-card skipped">
                <h3>Skipped</h3>
                <div class="value">{summary['total_skipped']}</div>
            </div>
            <div class="summary-card time">
                <h3>Total Time</h3>
                <div class="value">{summary['total_time']:.2f}s</div>
            </div>
        </div>
        
        <div class="progress-section">
            <h2>📊 Pass Rate</h2>
            <div class="progress-bar-container">
                <div class="progress-bar">{summary['pass_rate']:.1f}%</div>
            </div>
            <div class="progress-stats">
                <span>{summary['total_passed']} passed</span>
                <span>{summary['total_failures']} failed</span>
                <span>{summary['total_errors']} errors</span>
            </div>
        </div>
        
        <div class="test-classes-section">
            <h2>📋 Test Classes ({len(test_classes)})</h2>
"""
    
    # Add test class details
    if test_classes:
        for test_class in sorted(test_classes, key=lambda x: x['tests'], reverse=True):
            status_class = ""
            if test_class['failures'] > 0:
                status_class = "failed"
            elif test_class['errors'] > 0:
                status_class = "has-errors"
            
            html += f"""
            <div class="test-class-item {status_class}">
                <div class="test-class-header">
                    <div class="test-class-name">{test_class['class']}</div>
                    <div class="test-class-stats">
                        <span class="stat passed">{test_class['passed']} passed</span>
                        {f'<span class="stat failed">{test_class["failures"]} failed</span>' if test_class['failures'] > 0 else ''}
                        {f'<span class="stat errors">{test_class["errors"]} errors</span>' if test_class['errors'] > 0 else ''}
                        {f'<span class="stat skipped">{test_class["skipped"]} skipped</span>' if test_class['skipped'] > 0 else ''}
                    </div>
                </div>
                <div class="test-class-time">⏱️ {test_class['time']:.3f}s • {test_class['tests']} total tests</div>
            </div>
"""
    else:
        html += '<div class="no-tests">No test results found</div>'
    
    html += f"""
        </div>
        
        <div class="timestamp">
            <p>Report generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
            <p>JaCoCo Coverage: Disabled (Java 26 preview incompatibility)</p>
        </div>
        
        <div class="footer">
            <p>Built with ❤️ using Spring Boot and Java 26</p>
        </div>
    </div>
</body>
</html>
"""
    
    # Write HTML file
    os.makedirs(os.path.dirname(output_file), exist_ok=True)
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(html)
    
    print(f"✅ HTML Test Report generated: {output_file}")
    print(f"\n📊 Summary:")
    print(f"   Total Tests: {summary['total_tests']}")
    print(f"   Passed: {summary['total_passed']}")
    print(f"   Failed: {summary['total_failures']}")
    print(f"   Errors: {summary['total_errors']}")
    print(f"   Skipped: {summary['total_skipped']}")
    print(f"   Pass Rate: {summary['pass_rate']:.1f}%")
    print(f"   Total Time: {summary['total_time']:.2f}s")

def main():
    surefire_dir = sys.argv[1] if len(sys.argv) > 1 else "target/surefire-reports"
    output_file = sys.argv[2] if len(sys.argv) > 2 else "target/test-report/index.html"
    
    if not os.path.exists(surefire_dir):
        print(f"❌ Error: Surefire directory not found: {surefire_dir}")
        print("   Run tests first: mvn clean test")
        sys.exit(1)
    
    print("🔍 Parsing test results...")
    results = parse_test_results(surefire_dir)
    
    print("🎨 Generating HTML report...")
    generate_html_report(results, output_file)
    
    # Try to open the report
    try:
        import subprocess
        if sys.platform == 'darwin':
            subprocess.run(['open', output_file], check=False)
        elif sys.platform == 'linux':
            subprocess.run(['xdg-open', output_file], check=False)
        elif sys.platform == 'win32':
            subprocess.run(['start', output_file], check=False, shell=True)
    except Exception:
        print(f"\n📄 Open in browser: file://{os.path.abspath(output_file)}")

if __name__ == "__main__":
    main()
