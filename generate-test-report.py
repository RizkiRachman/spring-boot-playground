#!/usr/bin/env python3
"""
Generate comprehensive HTML test report from Maven Surefire XML results
"""

import os
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from datetime import datetime
import re


def escape_html(text):
    """Escape HTML special characters"""
    return text.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')


def categorize_test(test_name):
    """Categorize test based on name patterns"""
    categories = []
    name_lower = test_name.lower()
    
    if 'controller' in name_lower or 'rest' in name_lower or 'api' in name_lower:
        categories.append('controller')
    if 'service' in name_lower or 'business' in name_lower:
        categories.append('service')
    if 'repository' in name_lower or 'dao' in name_lower or 'database' in name_lower:
        categories.append('repository')
    if 'unit' in name_lower:
        categories.append('unit')
    if 'integration' in name_lower:
        categories.append('integration')
    if 'mock' in name_lower or 'stub' in name_lower:
        categories.append('mock')
    if 'security' in name_lower or 'auth' in name_lower:
        categories.append('security')
    if 'validation' in name_lower:
        categories.append('validation')
    if 'performance' in name_lower or 'benchmark' in name_lower:
        categories.append('performance')
    if not categories:
        categories.append('other')
    
    return categories


def get_stack_trace(testcase, status):
    """Extract stack trace from failed/errored test"""
    stack_trace = ""
    
    if status == 'failed':
        failure = testcase.find('failure')
        if failure is not None:
            stack_trace = failure.get('message', '')
            if failure.text:
                stack_trace += "\n" + failure.text
    elif status == 'error':
        error = testcase.find('error')
        if error is not None:
            stack_trace = error.get('message', '')
            if error.text:
                stack_trace += "\n" + error.text
    
    return escape_html(stack_trace)


def parse_test_results(surefire_dir):
    """Parse all Surefire XML test reports with detailed information"""
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
            
            full_class_name = root.get('name', 'Unknown')
            test_class = full_class_name.split('.')[-1]
            package = '.'.join(full_class_name.split('.')[:-1]) if '.' in full_class_name else ''
            
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
                stack_trace = ''
                
                # Check for failures, errors, or skipped
                if testcase.find('failure') is not None:
                    status = 'failed'
                    stack_trace = get_stack_trace(testcase, 'failed')
                elif testcase.find('error') is not None:
                    status = 'error'
                    stack_trace = get_stack_trace(testcase, 'error')
                elif testcase.find('skipped') is not None:
                    status = 'skipped'
                
                categories = categorize_test(name)
                
                test_cases.append({
                    'name': name,
                    'time': time_taken,
                    'status': status,
                    'categories': categories,
                    'stack_trace': stack_trace
                })
            
            # Count categories
            category_counts = {}
            for tc in test_cases:
                for cat in tc['categories']:
                    category_counts[cat] = category_counts.get(cat, 0) + 1
            
            results.append({
                'class': test_class,
                'package': package,
                'full_class': full_class_name,
                'tests': tests,
                'failures': failures,
                'errors': errors,
                'skipped': skipped,
                'passed': tests - failures - errors - skipped,
                'time': time,
                'test_cases': test_cases,
                'category_counts': category_counts
            })
            
            total_tests += tests
            total_failures += failures
            total_errors += errors
            total_skipped += skipped
            total_time += time
            
        except Exception as e:
            print(f"Warning: Could not parse {xml_file}: {e}", file=sys.stderr)
    
    # Calculate overall category distribution
    all_categories = {}
    for result in results:
        for tc in result['test_cases']:
            for cat in tc['categories']:
                if tc['status'] not in ['failed', 'error']:
                    all_categories[cat] = all_categories.get(cat, 0) + 1
    
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
        },
        'category_distribution': all_categories
    }


def generate_html_report(results, output_file):
    """Generate comprehensive HTML report with expandable sections"""
    summary = results['summary']
    test_classes = results['test_classes']
    category_distribution = results.get('category_distribution', {})
    
    # Calculate color based on pass rate
    if summary['pass_rate'] >= 90:
        status_color = "#10b981"
        status_text = "Excellent"
        status_class = "success"
    elif summary['pass_rate'] >= 70:
        status_color = "#f59e0b"
        status_text = "Good"
        status_class = "warning"
    else:
        status_color = "#ef4444"
        status_text = "Needs Improvement"
        status_class = "error"
    
    # Calculate pie chart data
    pie_data = [
        ('Passed', summary['total_passed'], '#10b981'),
        ('Failed', summary['total_failures'], '#ef4444'),
        ('Errors', summary['total_errors'], '#f59e0b'),
        ('Skipped', summary['total_skipped'], '#6b7280')
    ]
    
    # Generate category chart data
    cat_chart_data = sorted(category_distribution.items(), key=lambda x: x[1], reverse=True)[:8]
    
    # Generate HTML
    html = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring Boot Playground - Comprehensive Test Report</title>
    <style>
        * {{
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }}
        
        :root {{
            --primary: #3b82f6;
            --success: #10b981;
            --warning: #f59e0b;
            --error: #ef4444;
            --gray: #6b7280;
            --dark: #1f2937;
            --light: #f3f4f6;
            --white: #ffffff;
            --shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
            --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
        }}
        
        body {{
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            line-height: 1.6;
        }}
        
        @media (max-width: 768px) {{
            body {{
                padding: 10px;
            }}
        }}
        
        .container {{
            max-width: 1400px;
            margin: 0 auto;
        }}
        
        /* Header */
        .header {{
            background: var(--white);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: var(--shadow-lg);
        }}
        
        @media (max-width: 768px) {{
            .header {{
                padding: 20px;
            }}
        }}
        
        .header h1 {{
            color: var(--dark);
            font-size: 2.5em;
            margin-bottom: 10px;
        }}
        
        @media (max-width: 768px) {{
            .header h1 {{
                font-size: 1.8em;
            }}
        }}
        
        .header .subtitle {{
            color: var(--gray);
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
        
        /* Summary Cards */
        .summary-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        
        @media (max-width: 768px) {{
            .summary-grid {{
                grid-template-columns: repeat(2, 1fr);
                gap: 10px;
            }}
        }}
        
        .summary-card {{
            background: var(--white);
            border-radius: 15px;
            padding: 25px;
            box-shadow: var(--shadow);
            text-align: center;
            transition: transform 0.2s, box-shadow 0.2s;
        }}
        
        .summary-card:hover {{
            transform: translateY(-2px);
            box-shadow: var(--shadow-lg);
        }}
        
        .summary-card.total {{ border-top: 4px solid #3b82f6; }}
        .summary-card.passed {{ border-top: 4px solid #10b981; }}
        .summary-card.failed {{ border-top: 4px solid #ef4444; }}
        .summary-card.errors {{ border-top: 4px solid #f59e0b; }}
        .summary-card.skipped {{ border-top: 4px solid #6b7280; }}
        .summary-card.time {{ border-top: 4px solid #8b5cf6; }}
        .summary-card.rate {{ border-top: 4px solid {status_color}; }}
        
        .summary-card h3 {{
            color: var(--gray);
            font-size: 0.85em;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom: 10px;
        }}
        
        .summary-card .value {{
            font-size: 2.5em;
            font-weight: bold;
            color: var(--dark);
        }}
        
        @media (max-width: 768px) {{
            .summary-card .value {{
                font-size: 1.8em;
            }}
        }}
        
        /* Charts Section */
        .charts-section {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }}
        
        .chart-card {{
            background: var(--white);
            border-radius: 15px;
            padding: 30px;
            box-shadow: var(--shadow);
        }}
        
        .chart-card h2 {{
            color: var(--dark);
            margin-bottom: 20px;
            font-size: 1.3em;
        }}
        
        /* Pie Chart */
        .pie-chart-container {{
            display: flex;
            align-items: center;
            gap: 30px;
            flex-wrap: wrap;
        }}
        
        .pie-chart {{
            width: 200px;
            height: 200px;
            border-radius: 50%;
            background: conic-gradient(
                #10b981 0deg {summary['total_passed'] / summary['total_tests'] * 360 if summary['total_tests'] > 0 else 0}deg,
                #ef4444 {summary['total_passed'] / summary['total_tests'] * 360 if summary['total_tests'] > 0 else 0}deg {(summary['total_passed'] + summary['total_failures']) / summary['total_tests'] * 360 if summary['total_tests'] > 0 else 0}deg,
                #f59e0b {(summary['total_passed'] + summary['total_failures']) / summary['total_tests'] * 360 if summary['total_tests'] > 0 else 0}deg {(summary['total_passed'] + summary['total_failures'] + summary['total_errors']) / summary['total_tests'] * 360 if summary['total_tests'] > 0 else 0}deg,
                #6b7280 {(summary['total_passed'] + summary['total_failures'] + summary['total_errors']) / summary['total_tests'] * 360 if summary['total_tests'] > 0 else 0}deg 360deg
            );
            position: relative;
        }}
        
        .pie-chart-center {{
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 120px;
            height: 120px;
            background: var(--white);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5em;
            font-weight: bold;
            color: var(--dark);
        }}
        
        .chart-legend {{
            display: flex;
            flex-direction: column;
            gap: 10px;
        }}
        
        .legend-item {{
            display: flex;
            align-items: center;
            gap: 10px;
        }}
        
        .legend-color {{
            width: 20px;
            height: 20px;
            border-radius: 4px;
        }}
        
        .legend-label {{
            font-size: 0.95em;
            color: var(--dark);
        }}
        
        /* Bar Chart */
        .bar-chart {{
            display: flex;
            flex-direction: column;
            gap: 12px;
        }}
        
        .bar-item {{
            display: flex;
            align-items: center;
            gap: 10px;
        }}
        
        .bar-label {{
            min-width: 100px;
            font-size: 0.9em;
            color: var(--dark);
            text-transform: capitalize;
        }}
        
        .bar-track {{
            flex: 1;
            height: 24px;
            background: var(--light);
            border-radius: 12px;
            overflow: hidden;
            position: relative;
        }}
        
        .bar-fill {{
            height: 100%;
            border-radius: 12px;
            transition: width 0.5s ease;
            display: flex;
            align-items: center;
            justify-content: flex-end;
            padding-right: 8px;
            color: white;
            font-size: 0.8em;
            font-weight: bold;
        }}
        
        /* Progress Section */
        .progress-section {{
            background: var(--white);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: var(--shadow);
        }}
        
        .progress-section h2 {{
            color: var(--dark);
            margin-bottom: 20px;
        }}
        
        .progress-bar-container {{
            background: var(--light);
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
            color: var(--gray);
            font-size: 0.95em;
        }}
        
        @media (max-width: 768px) {{
            .progress-stats {{
                flex-direction: column;
                gap: 8px;
            }}
        }}
        
        /* Search and Filter Section */
        .filter-section {{
            background: var(--white);
            border-radius: 15px;
            padding: 20px 30px;
            margin-bottom: 30px;
            box-shadow: var(--shadow);
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
            align-items: center;
        }}
        
        .filter-input {{
            flex: 1;
            min-width: 250px;
            padding: 12px 16px;
            border: 2px solid var(--light);
            border-radius: 8px;
            font-size: 1em;
            transition: border-color 0.2s;
        }}
        
        .filter-input:focus {{
            outline: none;
            border-color: var(--primary);
        }}
        
        .filter-button {{
            padding: 12px 20px;
            border: none;
            border-radius: 8px;
            background: var(--primary);
            color: white;
            font-size: 0.9em;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.2s;
        }}
        
        .filter-button:hover {{
            background: #2563eb;
        }}
        
        .filter-button.active {{
            background: var(--dark);
        }}
        
        /* Test Classes Section */
        .test-classes-section {{
            background: var(--white);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: var(--shadow);
        }}
        
        .test-classes-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 10px;
        }}
        
        .test-classes-section h2 {{
            color: var(--dark);
        }}
        
        .test-classes-count {{
            color: var(--gray);
            font-size: 0.9em;
        }}
        
        /* Accordion */
        .accordion-item {{
            border-radius: 10px;
            margin-bottom: 15px;
            border: 1px solid var(--light);
            overflow: hidden;
        }}
        
        .accordion-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
            background: var(--light);
            cursor: pointer;
            transition: background 0.2s;
            user-select: none;
        }}
        
        .accordion-header:hover {{
            background: #e5e7eb;
        }}
        
        .accordion-header.failed {{
            background: #fef2f2;
            border-left: 4px solid var(--error);
        }}
        
        .accordion-header.errors {{
            background: #fffbeb;
            border-left: 4px solid var(--warning);
        }}
        
        .accordion-header.passed {{
            background: #f0fdf4;
            border-left: 4px solid var(--success);
        }}
        
        .accordion-header.skipped {{
            background: #f3f4f6;
            border-left: 4px solid var(--gray);
        }}
        
        .accordion-title {{
            display: flex;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }}
        
        .accordion-icon {{
            font-size: 1.2em;
            transition: transform 0.3s ease;
        }}
        
        .accordion-item.expanded .accordion-icon {{
            transform: rotate(90deg);
        }}
        
        .accordion-class-name {{
            font-weight: bold;
            color: var(--dark);
            font-size: 1.1em;
        }}
        
        .accordion-package {{
            color: var(--gray);
            font-size: 0.85em;
        }}
        
        .accordion-stats {{
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }}
        
        .stat {{
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 0.85em;
            font-weight: bold;
        }}
        
        .stat.passed {{ background: #d1fae5; color: #065f46; }}
        .stat.failed {{ background: #fee2e2; color: #991b1b; }}
        .stat.error {{ background: #fef3c7; color: #92400e; }}
        .stat.skipped {{ background: #f3f4f6; color: #4b5563; }}
        
        .accordion-content {{
            max-height: 0;
            overflow: hidden;
            transition: max-height 0.3s ease-out;
        }}
        
        .accordion-item.expanded .accordion-content {{
            max-height: 50000px;
        }}
        
        .accordion-body {{
            padding: 20px;
            background: var(--white);
        }}
        
        /* Test Cases Table */
        .test-cases-table {{
            width: 100%;
            border-collapse: collapse;
        }}
        
        .test-cases-table th,
        .test-cases-table td {{
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid var(--light);
        }}
        
        .test-cases-table th {{
            background: var(--light);
            font-weight: 600;
            color: var(--dark);
            font-size: 0.9em;
        }}
        
        @media (max-width: 768px) {{
            .test-cases-table {{
                display: block;
                overflow-x: auto;
            }}
        }}
        
        .test-case-row {{
            cursor: pointer;
            transition: background 0.2s;
        }}
        
        .test-case-row:hover {{
            background: #f9fafb;
        }}
        
        .test-case-row.failed {{ background: #fef2f2; }}
        .test-case-row.error {{ background: #fffbeb; }}
        .test-case-row.skipped {{ background: #f3f4f6; opacity: 0.7; }}
        
        .test-case-row.failed:hover {{ background: #fee2e2; }}
        .test-case-row.error:hover {{ background: #fef3c7; }}
        
        .test-case-name {{
            font-weight: 500;
            color: var(--dark);
        }}
        
        .test-case-time {{
            color: var(--gray);
            font-size: 0.9em;
        }}
        
        .status-badge-small {{
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 0.8em;
            font-weight: bold;
        }}
        
        .status-badge-small.passed {{
            background: #d1fae5;
            color: #065f46;
        }}
        
        .status-badge-small.failed {{
            background: #fee2e2;
            color: #991b1b;
        }}
        
        .status-badge-small.error {{
            background: #fef3c7;
            color: #92400e;
        }}
        
        .status-badge-small.skipped {{
            background: #f3f4f6;
            color: #4b5563;
        }}
        
        /* Categories */
        .category-tags {{
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }}
        
        .category-tag {{
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 0.75em;
            background: #e0e7ff;
            color: #3730a3;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }}
        
        /* Error Details */
        .error-details {{
            margin-top: 15px;
            padding: 15px;
            background: #fef2f2;
            border: 1px solid #fee2e2;
            border-radius: 8px;
            display: none;
        }}
        
        .error-details.show {{
            display: block;
        }}
        
        .error-details-header {{
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }}
        
        .error-details-title {{
            font-weight: bold;
            color: #991b1b;
        }}
        
        .error-close {{
            cursor: pointer;
            font-size: 1.2em;
            color: #991b1b;
        }}
        
        .error-stack {{
            background: white;
            padding: 15px;
            border-radius: 6px;
            overflow-x: auto;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-size: 0.85em;
            line-height: 1.5;
            color: #374151;
            white-space: pre-wrap;
            word-break: break-word;
        }}
        
        .expand-error-btn {{
            background: none;
            border: none;
            color: #ef4444;
            cursor: pointer;
            font-size: 0.9em;
            text-decoration: underline;
            padding: 0;
        }}
        
        /* No Results */
        .no-results {{
            text-align: center;
            padding: 40px;
            color: var(--gray);
        }}
        
        .no-tests {{
            text-align: center;
            padding: 40px;
            color: var(--gray);
        }}
        
        /* Footer */
        .footer {{
            text-align: center;
            margin-top: 40px;
            color: rgba(255,255,255,0.8);
            font-size: 0.9em;
        }}
        
        .timestamp {{
            text-align: center;
            color: var(--gray);
            margin-top: 20px;
            font-size: 0.9em;
            padding: 20px;
            background: var(--white);
            border-radius: 15px;
            box-shadow: var(--shadow);
        }}
        
        /* Expand All / Collapse All */
        .action-buttons {{
            display: flex;
            gap: 10px;
        }}
        
        .action-btn {{
            padding: 8px 16px;
            border: 1px solid var(--primary);
            background: white;
            color: var(--primary);
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.85em;
            transition: all 0.2s;
        }}
        
        .action-btn:hover {{
            background: var(--primary);
            color: white;
        }}
        
        /* Quick Stats */
        .quick-stats {{
            display: flex;
            justify-content: space-around;
            padding: 15px;
            background: var(--light);
            border-radius: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
            gap: 15px;
        }}
        
        .quick-stat {{
            text-align: center;
        }}
        
        .quick-stat-value {{
            font-size: 1.5em;
            font-weight: bold;
            color: var(--dark);
        }}
        
        .quick-stat-label {{
            font-size: 0.8em;
            color: var(--gray);
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🧪 Comprehensive Test Report</h1>
            <div class="subtitle">Spring Boot Playground - Unit & Integration Testing</div>
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
        
        <div class="charts-section">
            <div class="chart-card">
                <h2>📊 Test Results Distribution</h2>
                <div class="pie-chart-container">
                    <div class="pie-chart">
                        <div class="pie-chart-center">{summary['pass_rate']:.0f}%</div>
                    </div>
                    <div class="chart-legend">
                        <div class="legend-item">
                            <div class="legend-color" style="background: #10b981;"></div>
                            <span class="legend-label">Passed ({summary['total_passed']})</span>
                        </div>
                        <div class="legend-item">
                            <div class="legend-color" style="background: #ef4444;"></div>
                            <span class="legend-label">Failed ({summary['total_failures']})</span>
                        </div>
                        <div class="legend-item">
                            <div class="legend-color" style="background: #f59e0b;"></div>
                            <span class="legend-label">Errors ({summary['total_errors']})</span>
                        </div>
                        <div class="legend-item">
                            <div class="legend-color" style="background: #6b7280;"></div>
                            <span class="legend-label">Skipped ({summary['total_skipped']})</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="chart-card">
                <h2>📈 Test Categories</h2>
                <div class="bar-chart">
"""
    
    # Add category bars
    if cat_chart_data:
        max_cat = max(cat_chart_data, key=lambda x: x[1])[1] if cat_chart_data else 1
        colors = ['#3b82f6', '#10b981', '#8b5cf6', '#f59e0b', '#ef4444', '#06b6d4', '#84cc16', '#ec4899']
        for i, (cat, count) in enumerate(cat_chart_data):
            percentage = count / max_cat * 100 if max_cat > 0 else 0
            color = colors[i % len(colors)]
            html += f"""
                    <div class="bar-item">
                        <span class="bar-label">{cat}</span>
                        <div class="bar-track">
                            <div class="bar-fill" style="width: {percentage}%; background: {color};">{count}</div>
                        </div>
                    </div>
"""
    else:
        html += '<div class="no-results">No categories found</div>'
    
    html += f"""
                </div>
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
        
        <div class="filter-section">
            <input type="text" class="filter-input" id="searchInput" placeholder="🔍 Search test classes, methods, or categories...">
            <button class="filter-button" onclick="filterTests('all')">All</button>
            <button class="filter-button" onclick="filterTests('passed')">Passed</button>
            <button class="filter-button" onclick="filterTests('failed')">Failed</button>
            <button class="filter-button" onclick="filterTests('error')">Errors</button>
            <button class="filter-button" onclick="filterTests('skipped')">Skipped</button>
        </div>
        
        <div class="test-classes-section">
            <div class="test-classes-header">
                <h2>📋 Test Classes ({len(test_classes)})</h2>
                <div class="action-buttons">
                    <button class="action-btn" onclick="expandAll()">Expand All</button>
                    <button class="action-btn" onclick="collapseAll()">Collapse All</button>
                </div>
            </div>
            <div id="testClassesContainer">
"""
    
    # Add test class accordions
    if test_classes:
        # Sort: failed first, then by number of tests
        sorted_classes = sorted(test_classes, key=lambda x: (x['failures'] + x['errors'] == 0, -x['tests']))
        
        for test_class in sorted_classes:
            status = 'passed'
            if test_class['failures'] > 0:
                status = 'failed'
            elif test_class['errors'] > 0:
                status = 'errors'
            elif test_class['skipped'] == test_class['tests']:
                status = 'skipped'
            
            html += f"""
                <div class="accordion-item" data-status="{status}" data-class="{test_class['class'].lower()}">
                    <div class="accordion-header {status}" onclick="toggleAccordion(this)">
                        <div class="accordion-title">
                            <span class="accordion-icon">▶</span>
                            <div>
                                <div class="accordion-class-name">{test_class['class']}</div>
                                <div class="accordion-package">{test_class['package']}</div>
                            </div>
                        </div>
                        <div class="accordion-stats">
                            <span class="stat passed">{test_class['passed']} ✓</span>
                            {f'<span class="stat failed">{test_class["failures"]} ✗</span>' if test_class['failures'] > 0 else ''}
                            {f'<span class="stat error">{test_class["errors"]} ⚠</span>' if test_class['errors'] > 0 else ''}
                            {f'<span class="stat skipped">{test_class["skipped"]} ⊘</span>' if test_class['skipped'] > 0 else ''}
                            <span style="color: var(--gray); margin-left: 10px; font-size: 0.9em;">⏱ {test_class['time']:.3f}s</span>
                        </div>
                    </div>
                    <div class="accordion-content">
                        <div class="accordion-body">
                            <div class="quick-stats">
                                <div class="quick-stat">
                                    <div class="quick-stat-value">{test_class['tests']}</div>
                                    <div class="quick-stat-label">Total Tests</div>
                                </div>
                                <div class="quick-stat">
                                    <div class="quick-stat-value">{test_class['time']:.3f}s</div>
                                    <div class="quick-stat-label">Duration</div>
                                </div>
                                <div class="quick-stat">
                                    <div class="quick-stat-value">{(test_class['time'] / test_class['tests'] if test_class['tests'] > 0 else 0):.3f}s</div>
                                    <div class="quick-stat-label">Avg Time</div>
                                </div>
                                <div class="quick-stat">
                                    <div class="quick-stat-value">{len(test_class['category_counts'])}</div>
                                    <div class="quick-stat-label">Categories</div>
                                </div>
                            </div>
                            <table class="test-cases-table">
                                <thead>
                                    <tr>
                                        <th>Status</th>
                                        <th>Test Name</th>
                                        <th>Categories</th>
                                        <th>Time</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
"""
            # Sort test cases: failed first, then by time
            sorted_cases = sorted(test_class['test_cases'], 
                                   key=lambda x: (0 if x['status'] == 'passed' else 1, -x['time']))
            
            for i, tc in enumerate(sorted_cases):
                status_icon = '✓' if tc['status'] == 'passed' else '✗' if tc['status'] == 'failed' else '⚠' if tc['status'] == 'error' else '⊘'
                categories_html = ''.join([f'<span class="category-tag">{cat}</span>' for cat in tc['categories']])
                error_btn = f'<button class="expand-error-btn" onclick="showError(\'error-{test_class["class"]}-{i}\', event)">View Details</button>' if tc['status'] in ['failed', 'error'] else '-'
                
                html += f"""
                                    <tr class="test-case-row {tc['status']}" data-method="{tc['name'].lower()}" data-categories="{' '.join(tc['categories'])}">
                                        <td><span class="status-badge-small {tc['status']}">{status_icon} {tc['status'].title()}</span></td>
                                        <td class="test-case-name">{tc['name']}</td>
                                        <td><div class="category-tags">{categories_html}</div></td>
                                        <td class="test-case-time">{tc['time']:.3f}s</td>
                                        <td>{error_btn}</td>
                                    </tr>
"""
                if tc['stack_trace']:
                    html += f"""
                                    <tr id="error-{test_class['class']}-{i}" class="error-details-row" style="display: none;">
                                        <td colspan="5">
                                            <div class="error-details">
                                                <div class="error-details-header">
                                                    <span class="error-details-title">🚨 Error Details</span>
                                                    <span class="error-close" onclick="hideError('error-{test_class['class']}-{i}', event)">✕</span>
                                                </div>
                                                <pre class="error-stack">{tc['stack_trace']}</pre>
                                            </div>
                                        </td>
                                    </tr>
"""
            
            html += """
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
"""
    else:
        html += '<div class="no-tests">No test results found</div>'
    
    html += f"""
            </div>
        </div>
        
        <div class="timestamp">
            <p>Report generated: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</p>
            <p>JaCoCo Coverage: Disabled (Java 26 preview incompatibility)</p>
        </div>
        
        <div class="footer">
            <p>Built with ❤️ using Spring Boot and Java 26</p>
        </div>
    </div>
    
    <script>
        // Toggle accordion
        function toggleAccordion(header) {{
            const item = header.parentElement;
            item.classList.toggle('expanded');
        }}
        
        // Expand all accordions
        function expandAll() {{
            document.querySelectorAll('.accordion-item').forEach(item => {{
                item.classList.add('expanded');
            }});
        }}
        
        // Collapse all accordions
        function collapseAll() {{
            document.querySelectorAll('.accordion-item').forEach(item => {{
                item.classList.remove('expanded');
            }});
        }}
        
        // Show error details
        function showError(errorId, event) {{
            event.stopPropagation();
            const errorRow = document.getElementById(errorId);
            if (errorRow) {{
                errorRow.style.display = 'table-row';
                const errorDetails = errorRow.querySelector('.error-details');
                if (errorDetails) {{
                    errorDetails.classList.add('show');
                }}
            }}
        }}
        
        // Hide error details
        function hideError(errorId, event) {{
            event.stopPropagation();
            const errorRow = document.getElementById(errorId);
            if (errorRow) {{
                errorRow.style.display = 'none';
                const errorDetails = errorRow.querySelector('.error-details');
                if (errorDetails) {{
                    errorDetails.classList.remove('show');
                }}
            }}
        }}
        
        // Filter tests
        function filterTests(filter) {{
            const items = document.querySelectorAll('.accordion-item');
            const buttons = document.querySelectorAll('.filter-button');
            
            // Update button states
            buttons.forEach(btn => btn.classList.remove('active'));
            event.target.classList.add('active');
            
            items.forEach(item => {{
                if (filter === 'all' || item.dataset.status === filter) {{
                    item.style.display = 'block';
                }} else {{
                    item.style.display = 'none';
                }}
            }});
        }}
        
        // Search functionality
        document.getElementById('searchInput').addEventListener('input', function(e) {{
            const searchTerm = e.target.value.toLowerCase();
            const items = document.querySelectorAll('.accordion-item');
            
            items.forEach(item => {{
                const className = item.dataset.class;
                const testRows = item.querySelectorAll('.test-case-row');
                let hasMatch = className.includes(searchTerm);
                
                testRows.forEach(row => {{
                    const methodName = row.dataset.method;
                    const categories = row.dataset.categories;
                    
                    if (methodName.includes(searchTerm) || categories.includes(searchTerm)) {{
                        row.style.display = 'table-row';
                        hasMatch = true;
                        // Auto-expand if there's a match
                        if (searchTerm.length > 0) {{
                            item.classList.add('expanded');
                        }}
                    }} else {{
                        row.style.display = 'none';
                    }}
                }});
                
                item.style.display = hasMatch ? 'block' : 'none';
            }});
        }});
        
        // Keyboard shortcuts
        document.addEventListener('keydown', function(e) {{
            if (e.key === 'Escape') {{
                collapseAll();
            }} else if (e.key === 'e' && e.ctrlKey) {{
                e.preventDefault();
                expandAll();
            }}
        }});
        
        // Initialize: Expand failed tests by default
        document.addEventListener('DOMContentLoaded', function() {{
            document.querySelectorAll('.accordion-item[data-status="failed"], .accordion-item[data-status="errors"]').forEach(item => {{
                item.classList.add('expanded');
            }});
        }});
    </script>
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
    
    print("🎨 Generating HTML report with detailed test information...")
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
