package com.integra.Main;

import com.integra.DB.JDBC_Connection;
import com.integra.Exception.CustomeException;
import com.integra.daoimpl.DepartmentDAOImpl;
import com.integra.daoimpl.EmployeeDAOImpl;
import com.integra.daoimpl.ProjectDAOImpl;
import com.integra.dto.DepartmentDTO;
import com.integra.dto.EmployeeDTO;
import com.integra.dto.ProjectDTO;
import com.integra.service.IDepartmentService;
import com.integra.service.IEmployeeService;
import com.integra.service.IProjectService;
import com.integra.serviceimpl.DepartmentServiceImpl;
import com.integra.serviceimpl.EmployeeServiceImpl;
import com.integra.serviceimpl.ProjectServiceImpl;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main1 {

	public static void main(String[] args) throws CustomeException, SQLException {
		File propertiesFile = new File("db.properties");
		Scanner scanner = null;
		JDBC_Connection jdbcConnection = null;

		try {
			// Initialize JDBC connection and services
			scanner = new Scanner(System.in);
			jdbcConnection = new JDBC_Connection(propertiesFile);

			// Initialize department-related services
			DepartmentDAOImpl departmentDAO = new DepartmentDAOImpl(jdbcConnection);
			IDepartmentService departmentService = new DepartmentServiceImpl(departmentDAO);

			// Initialize employee-related services
			EmployeeDAOImpl employeeDAO = new EmployeeDAOImpl(jdbcConnection);
			IEmployeeService employeeService = new EmployeeServiceImpl(employeeDAO);

			ProjectDAOImpl projectDAO = new ProjectDAOImpl(jdbcConnection);
			IProjectService projectService = new ProjectServiceImpl(projectDAO);

			// Login page
			boolean loggedIn = false;
			boolean isAdmin = false;
			String loggedInEmpId = null;

			EmployeeDAOImpl employeeDAO1 = new EmployeeDAOImpl(jdbcConnection);

			while (!loggedIn) {
				System.out.println("\n=== Login Page ===");
				System.out.print("Enter Employee id: "); // Using Employee ID as the username
				String empId = scanner.nextLine();
				System.out.print("Enter Password: ");
				String password = scanner.nextLine();

				// Check admin credentials
				if (employeeDAO1.isAdminUser(empId, password)) {
					isAdmin = true;
					loggedInEmpId = empId; // Save the logged-in admin's Employee ID
					System.out.println("Admin logged in successfully!");
					loggedIn = true;
				}
				// Check user credentials
				else if (employeeDAO1.isValidUser(empId, password)) {
					isAdmin = false;
					loggedInEmpId = empId; // Save the logged-in user's Employee ID
					System.out.println("User logged in successfully!");
					loggedIn = true;
				} else {
					System.out.println("Invalid credentials. Please try again.");
				}
			}

			// Main menu
			while (true) {
				System.out.println("\n=== Main Menu ===");
				System.out.println("1. Manage Departments");
				System.out.println("2. Manage Employees");
				System.out.println("3. Manage Projects");
				System.out.println("4. Exit");
				System.out.print("Enter your choice: ");
				int choice = scanner.nextInt();
				scanner.nextLine(); // Consume newline

				switch (choice) {
				case 1: // Department Management
					handleDepartmentManagement(scanner, departmentService, isAdmin);
					break;

				case 2: // Employee Management
					handleEmployeeManagement(scanner, employeeService, departmentService, isAdmin, loggedInEmpId);
					break;

				case 3: // Project Management
					handleProjectManagement(scanner, projectService, isAdmin);
					break;

				case 4: // Exit
					jdbcConnection.closeConnection();
					System.out.println("Exiting... Goodbye!");
					scanner.close();
					return;

				default:
					System.out.println("Invalid choice. Please try again.");
				}
			}
		} catch (SQLException e) {
			System.out.println("Database connection error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Ensure that resources are closed properly
			if (scanner != null) {
				scanner.close();
			}
			if (jdbcConnection != null) {
				jdbcConnection.closeConnection();
			}
		}
	}

	private static void handleDepartmentManagement(Scanner scanner, IDepartmentService departmentService,
			boolean isAdmin) {
		while (true) {
			if (isAdmin) {
				// Admin can access all operations
				System.out.println("\n=== Department Management ===");
				System.out.println("1. View Department by ID ");
				System.out.println("2. View All Departments ");
				System.out.println("3. Add Department");
				System.out.println("4. Update Department");
				System.out.println("5. Delete Department");
				System.out.println("6. Back to Main Menu");
				System.out.print("Enter your choice: ");
			} else {
				// Regular user can only view departments
				System.out.println("\n=== Department Management (View Only) ===");
				System.out.println("1. View Department by ID");
				System.out.println("6. Back to Main Menu");
				System.out.print("Enter your choice: ");
			}

			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume newline

			switch (choice) {
			case 1:
				try {
					if (isAdmin) {
						// Admin can view all departments or by ID
						List<DepartmentDTO> departments = departmentService.getAllDepartments();
						if (departments.isEmpty()) {
							System.out.println("No departments available.");
						} else {
							System.out.println("Select a department ID to view:");
							for (int i = 0; i < departments.size(); i++) {
								System.out.println((i + 1) + ". " + departments.get(i).getDepartmentName());
							}
							System.out.print("Enter the department number: ");
							int deptChoice = scanner.nextInt();
							scanner.nextLine();
							if (deptChoice >= 1 && deptChoice <= departments.size()) {
								DepartmentDTO department = departments.get(deptChoice - 1);
								System.out.println("Department Details:");
								System.out.println("ID: " + department.getDepartmentId());
								System.out.println("Name: " + department.getDepartmentName());
								System.out.println("Comments: " + department.getComments());
							} else {
								System.out.println("Invalid choice.");
							}
						}
					} else {
						// Regular users only see their department
						// Assuming user can access their own department directly (maybe via userId,
						// departmentId)
						List<DepartmentDTO> departments = departmentService.getAllDepartments();
						if (departments.isEmpty()) {
							System.out.println("No departments available.");
						} else {
							// Assuming the first department belongs to the current user
							DepartmentDTO department = departments.get(loggedInUserId); // You can replace this with actual
																			// user-specific
							// logic
							System.out.println("Your Department Details:");
							System.out.println("ID: " + department.getDepartmentId());
							System.out.println("Name: " + department.getDepartmentName());
							System.out.println("Comments: " + department.getComments());
						}
					}
				} catch (CustomeException e) {
					System.out.println("Error: " + e.getMessage());

				}
				break;

			case 2: // View All Departments (only available to admin)
				if (isAdmin) {
					try {
						List<DepartmentDTO> departments = departmentService.getAllDepartments();
						System.out.println("All Departments:");
						for (DepartmentDTO dept : departments) {
							System.out.println("ID: " + dept.getDepartmentId() + ", Name: " + dept.getDepartmentName()
									+ ", Comments: " + dept.getComments());
						}
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					System.out.println("You don't have permission to view all departments.");
				}
				break;

			case 3: // Add Department (only available to admin)
				if (isAdmin) {
					System.out.print("Enter department ID: ");
					int departmentId = scanner.nextInt();
					scanner.nextLine(); // Consume newline
					System.out.print("Enter department name: ");
					String name = scanner.nextLine();
					System.out.print("Enter comments: ");
					String comments = scanner.nextLine();
					try {
						DepartmentDTO newDepartment = new DepartmentDTO(departmentId, name, comments);
						boolean isAdded = departmentService.addDepartment(newDepartment);
						System.out.println(isAdded ? "Department added successfully!" : "Failed to add department.");
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					System.out.println("You don't have permission to add a department.");
				}
				break;

			case 4: // Update Department (only available to admin)
				if (isAdmin) {
					try {
						List<DepartmentDTO> departments = departmentService.getAllDepartments();
						if (departments.isEmpty()) {
							System.out.println("No departments available.");
						} else {
							System.out.println("Select a department ID to update:");
							for (int i = 0; i < departments.size(); i++) {
								System.out.println((i + 1) + ". " + departments.get(i).getDepartmentId());
							}
							System.out.print("Enter the department number: ");
							int deptChoice = scanner.nextInt();
							scanner.nextLine(); // Consume newline
							if (deptChoice >= 1 && deptChoice <= departments.size()) {
								DepartmentDTO department = departments.get(deptChoice - 1);
								System.out.println("Current Department Details:");
								System.out.println("ID: " + department.getDepartmentId());
								System.out.println("Name: " + department.getDepartmentName());
								System.out.println("Comments: " + department.getComments());

								System.out.print("Enter new department name: ");
								String updatedName = scanner.nextLine();
								System.out.print("Enter new comments: ");
								String updatedComments = scanner.nextLine();

								DepartmentDTO updatedDepartment = new DepartmentDTO(department.getDepartmentId(),
										updatedName, updatedComments);
								boolean isUpdated = departmentService.updateDepartment(updatedDepartment);
								System.out.println(isUpdated ? "Department updated successfully!"
										: "Failed to update department.");
							} else {
								System.out.println("Invalid choice.");
							}
						}
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					System.out.println("You don't have permission to update a department.");
				}
				break;

			case 5: // Delete Department (only available to admin)
				if (isAdmin) {
					try {
						List<DepartmentDTO> departments = departmentService.getAllDepartments();
						if (departments.isEmpty()) {
							System.out.println("No departments available.");
						} else {
							System.out.println("Select a department ID to delete:");
							for (int i = 0; i < departments.size(); i++) {
								System.out.println((i + 1) + ". " + departments.get(i).getDepartmentId());
							}
							System.out.print("Enter the department number: ");
							int deptChoice = scanner.nextInt();
							scanner.nextLine(); // Consume newline
							if (deptChoice >= 1 && deptChoice <= departments.size()) {
								DepartmentDTO department = departments.get(deptChoice - 1);
								System.out.println("Current Department Details:");
								System.out.println("ID: " + department.getDepartmentId());
								System.out.println("Name: " + department.getDepartmentName());
								System.out.println("Comments: " + department.getComments());

								System.out.println("Choose delete option: ");
								System.out.println("1. Soft Delete");
								System.out.println("2. Hard Delete");
								System.out.print("Enter your choice: ");
								int deleteChoice = scanner.nextInt();
								boolean isSoftDelete = deleteChoice == 1;

								boolean isDeleted = departmentService.deleteDepartment(department.getDepartmentId(),
										isSoftDelete);
								System.out.println(isDeleted
										? (isSoftDelete ? "Department soft-deleted successfully!"
												: "Department hard-deleted successfully!")
										: "Failed to delete department.");
							} else {
								System.out.println("Invalid choice.");
							}
						}
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					System.out.println("You don't have permission to delete a department.");
				}
				break;
			case 6: // Back to Main Menu
				return;

			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

//	private static void handleDepartmentManagement(Scanner scanner, IDepartmentService departmentService, boolean isAdmin,int loggedInEmpId) {
//	    if (isAdmin) {
//	        System.out.println("\n-- Department Management (Admin Access) --\n");
//	        System.out.println("1. View Department by ID");
//	        System.out.println("2. View All Departments");
//	        System.out.println("3. Add Department");
//	        System.out.println("4. Update Department");
//	        System.out.println("5. Delete Department");
//	        System.out.println("6. Back to Main Menu");
//	        System.out.print("Enter your choice: ");
//	        int choice = scanner.nextInt();
//	        scanner.nextLine(); // Consume newline
//
//	        switch (choice) {
//	        case 1: // View Department by ID
//	        	 try {
//	                    List<DepartmentDTO> departments = departmentService.getAllDepartments();
//	                    if (departments.isEmpty()) {
//	                        System.out.println("No departments available.");
//	                    } else {
//	                        System.out.println("Select a department Name to view:");
//	                        for (int i = 0; i < departments.size(); i++) {
//	                            System.out.println((i + 1) + ". " + departments.get(i).getDepartmentName());
//	                        }
//	                        System.out.print("Enter the department number: ");
//	                        int deptChoice = scanner.nextInt();
//	                        scanner.nextLine(); // Consume newline
//	                        if (deptChoice >= 1 && deptChoice <= departments.size()) {
//	                            DepartmentDTO department = departments.get(deptChoice - 1);
//	                            System.out.println("Department Details:");
//	                            System.out.println("ID: " + department.getDepartmentId());
//	                            System.out.println("Name: " + department.getDepartmentName());
//	                            System.out.println("Comments: " + department.getComments());
//	                        } else {
//	                            System.out.println("Invalid choice.");
//	                        }
//	                    }
//	                } catch (CustomeException e) {
//	                    System.out.println("Error: " + e.getMessage());
//	                }
//	                break;
//
//	        case 2: // View All Departments (only available to admin)
//                if (isAdmin) {
//                    try {
//                        List<DepartmentDTO> departments = departmentService.getAllDepartments();
//                        System.out.println("All Departments:");
//                        for (DepartmentDTO dept : departments) {
//                            System.out.println("ID: " + dept.getDepartmentId() + ", Name: " + dept.getDepartmentName()
//                                    + ", Comments: " + dept.getComments());
//                        }
//                    } catch (CustomeException e) {
//                        System.out.println("Error: " + e.getMessage());
//                    }
//                } else {
//                    System.out.println("You don't have permission to view all departments.");
//                }
//                break;
//
//            case 3: // Add Department (only available to admin)
//                if (isAdmin) {
//                    System.out.print("Enter department ID: ");
//                    int departmentId = scanner.nextInt();
//                    scanner.nextLine(); // Consume newline
//                    System.out.print("Enter department name: ");
//                    String name = scanner.nextLine();
//                    System.out.print("Enter comments: ");
//                    String comments = scanner.nextLine();
//                    try {
//                        DepartmentDTO newDepartment = new DepartmentDTO(departmentId, name, comments);
//                        boolean isAdded = departmentService.addDepartment(newDepartment);
//                        System.out.println(isAdded ? "Department added successfully!" : "Failed to add department.");
//                    } catch (CustomeException e) {
//                        System.out.println("Error: " + e.getMessage());
//                    }
//                } else {
//                    System.out.println("You don't have permission to add a department.");
//                }
//                break;
//
//            case 4: // Update Department (only available to admin)
//                if (isAdmin) {
//                    try {
//                        List<DepartmentDTO> departments = departmentService.getAllDepartments();
//                        if (departments.isEmpty()) {
//                            System.out.println("No departments available.");
//                        } else {
//                            System.out.println("Select a department ID to update:");
//                            for (int i = 0; i < departments.size(); i++) {
//                                System.out.println((i + 1) + ". " + departments.get(i).getDepartmentId());
//                            }
//                            System.out.print("Enter the department number: ");
//                            int deptChoice = scanner.nextInt();
//                            scanner.nextLine(); // Consume newline
//                            if (deptChoice >= 1 && deptChoice <= departments.size()) {
//                                DepartmentDTO department = departments.get(deptChoice - 1);
//                                System.out.println("Current Department Details:");
//                                System.out.println("ID: " + department.getDepartmentId());
//                                System.out.println("Name: " + department.getDepartmentName());
//                                System.out.println("Comments: " + department.getComments());
//
//                                System.out.print("Enter new department name: ");
//                                String updatedName = scanner.nextLine();
//                                System.out.print("Enter new comments: ");
//                                String updatedComments = scanner.nextLine();
//
//                                DepartmentDTO updatedDepartment = new DepartmentDTO(department.getDepartmentId(),
//                                        updatedName, updatedComments);
//                                boolean isUpdated = departmentService.updateDepartment(updatedDepartment);
//                                System.out.println(
//                                        isUpdated ? "Department updated successfully!" : "Failed to update department.");
//                            } else {
//                                System.out.println("Invalid choice.");
//                            }
//                        }
//                    } catch (CustomeException e) {
//                        System.out.println("Error: " + e.getMessage());
//                    }
//                } else {
//                    System.out.println("You don't have permission to update a department.");
//                }
//                break;
//
//            case 5: // Delete Department (only available to admin)
//                if (isAdmin) {
//                    try {
//                        List<DepartmentDTO> departments = departmentService.getAllDepartments();
//                        if (departments.isEmpty()) {
//                            System.out.println("No departments available.");
//                        } else {
//                            System.out.println("Select a department ID to delete:");
//                            for (int i = 0; i < departments.size(); i++) {
//                                System.out.println((i + 1) + ". " + departments.get(i).getDepartmentId());
//                            }
//                            System.out.print("Enter the department number: ");
//                            int deptChoice = scanner.nextInt();
//                            scanner.nextLine(); // Consume newline
//                            if (deptChoice >= 1 && deptChoice <= departments.size()) {
//                                DepartmentDTO department = departments.get(deptChoice - 1);
//                                System.out.println("Current Department Details:");
//                                System.out.println("ID: " + department.getDepartmentId());
//                                System.out.println("Name: " + department.getDepartmentName());
//                                System.out.println("Comments: " + department.getComments());
//
//                                System.out.println("Choose delete option: ");
//                                System.out.println("1. Soft Delete");
//                                System.out.println("2. Hard Delete");
//                                System.out.print("Enter your choice: ");
//                                int deleteChoice = scanner.nextInt();
//                                boolean isSoftDelete = deleteChoice == 1;
//
//                                boolean isDeleted = departmentService.deleteDepartment(department.getDepartmentId(),
//                                        isSoftDelete);
//                                System.out.println(isDeleted
//                                        ? (isSoftDelete ? "Department soft-deleted successfully!"
//                                                : "Department hard-deleted successfully!")
//                                        : "Failed to delete department.");
//                            } else {
//                                System.out.println("Invalid choice.");
//                            }
//                        }
//                    } catch (CustomeException e) {
//                        System.out.println("Error: " + e.getMessage());
//                    }
//                } else {
//                    System.out.println("You don't have permission to delete a department.");
//                }
//                break;
//	        case 6: // Back to Main Menu
//	            return;
//	        default:
//	            System.out.println("Invalid choice. Please try again.");
//	            break;
//	        }
//	    } else {
//	        System.out.println("\n-- Department Management (User Access) --\n");
//	        System.out.println("1. View Department by ID");
//	        System.out.println("2. Back to Main Menu");
//	        System.out.print("Enter your choice: ");
//	        int choice = scanner.nextInt();
//	        scanner.nextLine(); // Consume newline
//
//	        switch (choice) {
//	        case 1: // View Department by ID
//	            System.out.print("Enter Department ID: ");
//	            int deptId = scanner.nextInt();
//
//	            try {
//	                // Fetch the department details using the entered department ID
//	                DepartmentDTO department = departmentService.getDepartmentById(loggedInEmpId);
//	                if (department != null) {
//	                    // Display the department details in a readable format
//	                    System.out.println("Department Details:");
//	                    System.out.println("ID: " + department.getDepartmentId());
//	                    System.out.println("Name: " + department.getDepartmentName());
//	                    System.out.println("Comments: " + department.getComments());
//	                } else {
//	                    System.out.println("Department not found.");
//	                }
//	            } catch (CustomeException e) {
//	                System.out.println("Error: " + e.getMessage());
//	            }
//	            break;
//
//	        case 2: // Back to Main Menu
//	            System.out.println("Returning to Main Menu...");
//	            return;
//	        default:
//	            System.out.println("Invalid choice. Please try again.");
//	            break;
//	        }
//	    }
//	}

	private static void handleEmployeeManagement(Scanner scanner, IEmployeeService employeeService,
			IDepartmentService departmentService, boolean isAdmin, String loggedInEmpId) throws CustomeException {
		if (isAdmin) {
			System.out.println("\n-- Employee Management (Admin Access) --\n");
			System.out.println("1. Add Employee");
			System.out.println("2. View Employee by ID");
			System.out.println("3. View All Employees");
			System.out.println("4. Update Employee");
			System.out.println("5. Delete Employee");
			System.out.println("6. Back to Main Menu");
			System.out.print("Enter your choice: ");
			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume newline

			switch (choice) {
			case 1: // Add Employee (Admin only)
				if (isAdmin) {
					// Display all available departments
					List<DepartmentDTO> departments = departmentService.getAllDepartments();
					System.out.println("Available Departments:");
					for (DepartmentDTO dept : departments) {
						System.out.println("ID: " + dept.getDepartmentId() + " - Name: " + dept.getDepartmentName());
					}

					// Prompt user to select a department
					System.out.print("Enter department ID from the list above: ");
					int empDeptId = scanner.nextInt();
					scanner.nextLine(); // Consume newline

					// Validate selected department ID
					boolean isValidDeptId = false;
					while (!isValidDeptId) {
						try {
							if (departmentService.isDepartmentExists(empDeptId)) {
								isValidDeptId = true; // Valid department ID
							} else {
								System.out.println("Invalid Department ID. Please select from the list above.");
								System.out.print("Enter department ID: ");
								empDeptId = scanner.nextInt();
								scanner.nextLine(); // Consume newline
							}
						} catch (CustomeException e) {
							System.out.println("Error checking department existence: " + e.getMessage());
						}
					}

					// Continue collecting employee details
					System.out.print("Enter employee ID: ");
					String empId = scanner.next();
					scanner.nextLine(); // Consume newline
					System.out.print("Enter employee name: ");
					String empName = scanner.nextLine();
					System.out.print("Enter qualification (BE/BTech/BSC/BCA/MCA): ");
					String qualification = scanner.nextLine();
					System.out.print("Enter phone number: ");
					String phoneNumber = scanner.nextLine();
					System.out.print("Enter marital status (M(Married)/S(Single)/D(Divorced)/W(Widowed)): ");
					String maritalStatus = scanner.nextLine();
					System.out.print("Enter gender (M(Male)/F(Female)/O(Others)): ");
					String gender = scanner.nextLine();
					System.out.print("Enter email ID: ");
					String emailId = scanner.nextLine();

					// Create EmployeeDTO and add the employee
					EmployeeDTO newEmployee = new EmployeeDTO(empId, empDeptId, empName, qualification, phoneNumber,
							maritalStatus, gender, emailId, false); // Admin status is false for new employees
					boolean isAdded = employeeService.addEmployee(newEmployee);
					System.out.println(isAdded ? "Employee added successfully!" : "Failed to add employee.");
				}
				break;

			case 2: // View Employee by ID
				// Fetch all employee IDs for dropdown
				List<EmployeeDTO> employees = employeeService.getAllEmployees();
				System.out.println("Select Employee ID from the list below:");
				int index = 1;
				for (EmployeeDTO emp : employees) {
					System.out.println(index++ + ". " + emp.getEmployeeId() + " - " + emp.getEmployeeName());
				}

				System.out.print("Enter your choice: ");
				int employeeChoice = scanner.nextInt();
				scanner.nextLine(); // Consume newline
				String selectedEmpId = employees.get(employeeChoice - 1).getEmployeeId();

				try {
					EmployeeDTO employee = employeeService.getEmployeeById(selectedEmpId);
					if (employee != null) {
						System.out.println("Employee Details:");
						System.out.println("ID: " + employee.getEmployeeId());
						System.out.println("Name: " + employee.getEmployeeName());
						System.out.println("Department ID: " + employee.getDepartmentId());
						System.out.println("Qualification: " + employee.getEmployeeQualification());
						System.out.println("Phone Number: " + employee.getPhoneNumber());
						System.out.println("Marital Status: " + employee.getMaritalStatus());
						System.out.println("Gender: " + employee.getGender());
						System.out.println("Email: " + employee.getEmailId());
					} else {
						System.out.println("Employee not found.");
					}
				} catch (CustomeException e) {
					System.out.println("Error: " + e.getMessage());
				}
				break;

			case 3: // View All Employees
				try {
					List<EmployeeDTO> allEmployees = employeeService.getAllEmployees();
					System.out.println("All Employees:");
					for (EmployeeDTO emp : allEmployees) {
						System.out.println("ID: " + emp.getEmployeeId() + ", Name: " + emp.getEmployeeName()
								+ ", Department ID: " + emp.getDepartmentId() + ", Qualification: "
								+ emp.getEmployeeQualification() + ", Phone: " + emp.getPhoneNumber()
								+ ", Marital Status: " + emp.getMaritalStatus() + ", Gender: " + emp.getGender()
								+ ", Email: " + emp.getEmailId());
					}
				} catch (CustomeException e) {
					e.printStackTrace();
					System.out.println("Error: " + e.getMessage());
				}
				break;

			case 4: // Update Employee
				// Fetch all employee IDs for dropdown
				List<EmployeeDTO> employeesToUpdate = employeeService.getAllEmployees();
				System.out.println("Select Employee ID from the list below:");
				index = 1;
				for (EmployeeDTO emp : employeesToUpdate) {
					System.out.println(index++ + ". " + emp.getEmployeeId() + " - " + emp.getEmployeeName());
				}

				System.out.print("Enter your choice: ");
				int updateChoice = scanner.nextInt();
				scanner.nextLine(); // Consume newline
				String empIdToUpdate = employeesToUpdate.get(updateChoice - 1).getEmployeeId();

				// Proceed with updating the employee
				System.out.print("Enter new employee name: ");
				String updatedEmpName = scanner.nextLine();
				System.out.print("Enter new department ID: ");
				int updatedDeptId = scanner.nextInt();
				scanner.nextLine(); // Consume newline
				System.out.print("Enter new qualification: ");
				String updatedQualification = scanner.nextLine();
				System.out.print("Enter new phone number: ");
				String updatedPhone = scanner.nextLine();
				System.out.print("Enter new marital status: ");
				String updatedMaritalStatus = scanner.nextLine();
				System.out.print("Enter new gender: ");
				String updatedGender = scanner.nextLine();
				System.out.print("Enter new email ID: ");
				String updatedEmailId = scanner.nextLine();

				try {
					EmployeeDTO updatedEmployee = new EmployeeDTO(empIdToUpdate, updatedEmpName, updatedDeptId,
							updatedQualification, updatedPhone, updatedMaritalStatus, updatedGender, updatedEmailId);
					boolean isUpdated = employeeService.updateEmployee(updatedEmployee);
					System.out.println(isUpdated ? "Employee updated successfully!" : "Failed to update employee.");
				} catch (CustomeException e) {
					System.out.println("Error: " + e.getMessage());
				}
				break;

			case 5: // Delete Employee
				// Fetch all employee IDs for dropdown
				List<EmployeeDTO> employeesToDelete = employeeService.getAllEmployees();
				System.out.println("Select Employee ID from the list below:");
				index = 1;
				for (EmployeeDTO emp : employeesToDelete) {
					System.out.println(index++ + ". " + emp.getEmployeeId() + " - " + emp.getEmployeeName());
				}

				System.out.print("Enter your choice: ");
				int deleteChoiceForEmp = scanner.nextInt();
				scanner.nextLine(); // Consume newline
				String empIdToDelete = employeesToDelete.get(deleteChoiceForEmp - 1).getEmployeeId();

				if (isAdmin) {
					// Admin can choose between soft delete and hard delete
					System.out.println("Choose delete option: ");
					System.out.println("1. Soft Delete");
					System.out.println("2. Hard Delete");
					System.out.print("Enter your choice: ");
					int deleteOption = scanner.nextInt();
					scanner.nextLine(); // Consume newline
					boolean isSoftDelete = deleteOption == 1;

					try {
						boolean isDeleted = employeeService.deleteEmployee(empIdToDelete, isSoftDelete);
						System.out
								.println(isDeleted
										? (isSoftDelete ? "Employee soft-deleted successfully!"
												: "Employee hard-deleted successfully!")
										: "Failed to delete employee.");
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					// User can only perform soft delete
					System.out.println("You can only perform a soft delete.");
					try {
						boolean isDeleted = employeeService.deleteEmployee(empIdToDelete, true);
						System.out.println(
								isDeleted ? "Employee soft-deleted successfully!" : "Failed to delete employee.");
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				}
				break;

			case 6: // Back to Main Menu
				return;

			default:
				System.out.println("Invalid choice. Please try again.");
				break;
			}
		} else {
			System.out.println("\n-- Employee Management (User Access) --\n");
			System.out.println("1. View My Details");
			System.out.println("2. Update My Details");
			System.out.println("3. Back to Main Menu");

			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume newline

			switch (choice) {
			case 1: // View My Details
				try {

					// Fetch the user details using the provided
					EmployeeDTO userDetails = employeeService.getEmployeeById(loggedInEmpId);
					if (userDetails != null) {
						// Display the user details in a readable format
						System.out.println("Your Employee Details:");
						System.out.println("ID: " + userDetails.getEmployeeId());
						System.out.println("Name: " + userDetails.getEmployeeName());
						System.out.println("Department ID: " + userDetails.getDepartmentId());
						System.out.println("Qualification: " + userDetails.getEmployeeQualification());
						System.out.println("Phone Number: " + userDetails.getPhoneNumber());
						System.out.println("Marital Status: " + userDetails.getMaritalStatus());
						System.out.println("Gender: " + userDetails.getGender());
						System.out.println("Email: " + userDetails.getEmailId());
					} else {
						System.out.println("User details not found.");
					}
				} catch (CustomeException e) {
					System.out.println("Error: " + e.getMessage());
				}
				break;

			case 2: // Update My Details

				try {
					EmployeeDTO userToUpdate = employeeService.getEmployeeById(loggedInEmpId);
					if (userToUpdate != null) {
						// Display current details of the user to be updated
						System.out.println("\nCurrent details:");
						System.out.println("Name: " + userToUpdate.getEmployeeName());
						System.out.println("Department ID: " + userToUpdate.getDepartmentId());
						System.out.println("Qualification: " + userToUpdate.getEmployeeQualification());
						System.out.println("Phone Number: " + userToUpdate.getPhoneNumber());
						System.out.println("Marital Status: " + userToUpdate.getMaritalStatus());
						System.out.println("Gender: " + userToUpdate.getGender());
						System.out.println("Email: " + userToUpdate.getEmailId());

						// Prompt the user to input updated details
						System.out.println("\nPlease enter updated details (leave blank to keep current):");

						System.out.print("Enter new name: ");
						String updatedName = scanner.nextLine();
						System.out.print("Enter new department ID: ");
						String updatedDeptIdInput = scanner.nextLine();
						Integer updatedDeptId = updatedDeptIdInput.isEmpty() ? null
								: Integer.parseInt(updatedDeptIdInput);

						System.out.print("Enter new qualification: ");
						String updatedQualification = scanner.nextLine();

						System.out.print("Enter new phone number: ");
						String updatedPhone = scanner.nextLine();

						System.out.print("Enter new marital status: ");
						String updatedMaritalStatus = scanner.nextLine();

						System.out.print("Enter new gender: ");
						String updatedGender = scanner.nextLine();

						// The email cannot be changed, so it remains the same.
						String updatedEmail = userToUpdate.getEmailId();

						// Create an updated EmployeeDTO object with new details
						EmployeeDTO updatedEmployee = new EmployeeDTO(userToUpdate.getEmployeeId(), // Keep the existing
																									// employee ID
								updatedName.isEmpty() ? userToUpdate.getEmployeeName() : updatedName,
								updatedDeptId != null ? updatedDeptId : userToUpdate.getDepartmentId(),
								updatedQualification.isEmpty() ? userToUpdate.getEmployeeQualification()
										: updatedQualification,
								updatedPhone.isEmpty() ? userToUpdate.getPhoneNumber() : updatedPhone,
								updatedMaritalStatus.isEmpty() ? userToUpdate.getMaritalStatus() : updatedMaritalStatus,
								updatedGender.isEmpty() ? userToUpdate.getGender() : updatedGender, updatedEmail // Email
																													// remains
																													// unchanged
						);

						// Call the service to update the employee details
						boolean isUpdated = employeeService.updateEmployee(updatedEmployee);
						System.out.println(isUpdated ? "Your details have been updated successfully!"
								: "Failed to update your details.");
					} else {
						System.out.println("User not found.");
					}
				} catch (CustomeException e) {
					System.out.println("Error: " + e.getMessage());
				}
				break;
			case 3:
				System.out.println("Returning to Main Menu...");
				return;
			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void handleProjectManagement(Scanner scanner, IProjectService projectService, boolean isAdmin)
			throws CustomeException {
		while (true) {
			if (isAdmin) {
				// Admin can access all operations
				System.out.println("\n=== Project Management ===");
				System.out.println("1. View Project by ID");
				System.out.println("2. View All Projects");
				System.out.println("3. Add Project");
				System.out.println("4. Update Project");
				System.out.println("5. Delete Project");
				System.out.println("6. Back to Main Menu");
				System.out.print("Enter your choice: ");
			} else {
				// Regular user can only view projects
				System.out.println("\n=== Project Management (View Only) ===");
				System.out.println("1. View Project by ID");
				System.out.println("6. Back to Main Menu");
				System.out.print("Enter your choice: ");
			}

			int choice = scanner.nextInt();
			scanner.nextLine(); // Consume newline

			switch (choice) {
			case 1: // View Project by ID
				try {
					List<ProjectDTO> projectsForView = projectService.getAllProjects();
					if (projectsForView.isEmpty()) {
						System.out.println("No projects available to view.");
						break;
					}

					System.out.println("Select Project ID from the list below:");
					int index = 1;
					for (ProjectDTO proj : projectsForView) {
						System.out.println(index++ + ". " + proj.getProjectId() + " - " + proj.getProjectName());
					}

					System.out.print("Enter your choice: ");
					int projectChoice = scanner.nextInt();
					scanner.nextLine(); // Consume newline
					int selectedProjectId = projectsForView.get(projectChoice - 1).getProjectId();

					ProjectDTO project = projectService.getProjectById(selectedProjectId);
					if (project != null) {
						System.out.println("Project Details:");
						System.out.println("ID: " + project.getProjectId());
						System.out.println("Name: " + project.getProjectName());
						System.out.println("Comments: " + project.getComments());
					} else {
						System.out.println("Project not found or marked as deleted.");
					}
				} catch (CustomeException e) {
					System.out.println("Error: " + e.getMessage());
				}
				break;

			case 2: // View All Projects
				try {
					List<ProjectDTO> allProjects = projectService.getAllProjects();
					if (allProjects.isEmpty()) {
						System.out.println("No projects available.");
					} else {
						System.out.println("All Projects:");
						for (ProjectDTO proj : allProjects) {
							System.out.println("ID: " + proj.getProjectId() + ", Name: " + proj.getProjectName()
									+ ", Comments: " + proj.getComments());
						}
					}
				} catch (CustomeException e) {
					System.out.println("Error: " + e.getMessage());
				}
				break;

			case 3: // Add Project (only available to admin)
				if (isAdmin) {
					System.out.print("Enter project ID: ");
					int projectId = scanner.nextInt();
					scanner.nextLine(); // Consume newline
					System.out.print("Enter project name: ");
					String projectName = scanner.nextLine();
					System.out.print("Enter comments: ");
					String projectComments = scanner.nextLine();

					try {
						ProjectDTO newProject = new ProjectDTO(projectId, projectName, projectComments);
						boolean isAdded = projectService.addProject(newProject);
						System.out.println(isAdded ? "Project added successfully!" : "Failed to add project.");
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					System.out.println("You don't have permission to add a project.");
				}
				break;

			case 4: // Update Project (only available to admin)
				if (isAdmin) {
					try {
						List<ProjectDTO> projectsForUpdate = projectService.getAllProjects();
						if (projectsForUpdate.isEmpty()) {
							System.out.println("No projects available to update.");
							break;
						}

						System.out.println("Select Project ID from the list below:");
						int index = 1;
						for (ProjectDTO proj : projectsForUpdate) {
							System.out.println(index++ + ". " + proj.getProjectId() + " - " + proj.getProjectName());
						}

						System.out.print("Enter your choice: ");
						int updateChoice = scanner.nextInt();
						scanner.nextLine(); // Consume newline
						int updateProjectId = projectsForUpdate.get(updateChoice - 1).getProjectId();

						System.out.print("Enter new project name: ");
						String updatedProjectName = scanner.nextLine();
						System.out.print("Enter new comments: ");
						String updatedComments = scanner.nextLine();

						try {
							ProjectDTO updatedProject = new ProjectDTO(updateProjectId, updatedProjectName,
									updatedComments);
							boolean isUpdated = projectService.updateProject(updatedProject);
							System.out
									.println(isUpdated ? "Project updated successfully!" : "Failed to update project.");
						} catch (CustomeException e) {
							System.out.println("Error: " + e.getMessage());
						}
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					System.out.println("You don't have permission to update a project.");
				}
				break;

			case 5: // Delete Project (only available to admin)
				if (isAdmin) {
					try {
						List<ProjectDTO> projectsForDelete = projectService.getAllProjects();
						if (projectsForDelete.isEmpty()) {
							System.out.println("No projects available to delete.");
							break;
						}

						System.out.println("Select Project ID from the list below:");
						int index = 1;
						for (ProjectDTO proj : projectsForDelete) {
							System.out.println(index++ + ". " + proj.getProjectId() + " - " + proj.getProjectName());
						}

						System.out.print("Enter your choice: ");
						int deleteChoice = scanner.nextInt();
						scanner.nextLine(); // Consume newline
						int deleteProjectId = projectsForDelete.get(deleteChoice - 1).getProjectId();

						System.out.println("Choose delete option: ");
						System.out.println("1. Soft Delete");
						System.out.println("2. Hard Delete");
						System.out.print("Enter your choice: ");
						int deleteOption = scanner.nextInt();
						boolean isSoftDelete = deleteOption == 1;

						try {
							boolean isDeleted = projectService.deleteProject(deleteProjectId, isSoftDelete);
							System.out
									.println(isDeleted
											? (isSoftDelete ? "Project soft-deleted successfully!"
													: "Project hard-deleted successfully!")
											: "Failed to delete project.");
						} catch (CustomeException e) {
							System.out.println("Error: " + e.getMessage());
						}
					} catch (CustomeException e) {
						System.out.println("Error: " + e.getMessage());
					}
				} else {
					System.out.println("You don't have permission to delete a project.");
				}
				break;

			case 6: // Back to Main Menu
				return;

			default:
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

}
