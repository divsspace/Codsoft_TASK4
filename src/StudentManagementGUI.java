import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Student implements Serializable {
    static final long serialVersionUID = 1L;

    String name;
    int rollNumber;
    String grade;

    public Student(String name, int rollNumber, String grade) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public String getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "Name=" + name + "\nRoll Number=" + rollNumber + "\nGrade=" + grade + "\n\n";
    }
}

class StudentManagementSystem {
    List<Student> students;
    static final String DATABASE = "Students.txt";

    public StudentManagementSystem() {
        students = new ArrayList<>();
        loadFromFile();
    }

    public void addStudent(Student student) {
        students.add(student);
        saveToFile();
    }

    public void removeStudent(int rollNumber) {
        students.removeIf(student -> student.getRollNumber() == rollNumber);
        saveToFile();
    }

    public Student searchStudent(int rollNumber) {
        for (Student student : students) {
            if (student.getRollNumber() == rollNumber) {
                return student;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream( DATABASE))) {
            oos.writeObject(students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    void loadFromFile() {
        File file = new File( DATABASE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream( DATABASE))) {
                students = (List<Student>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

public class StudentManagementGUI extends JFrame {
    StudentManagementSystem sms;
    JTextField nameField, rollNumberField, gradeField;
    JTextArea outputArea;

    public StudentManagementGUI() {
        sms = new StudentManagementSystem();
        setTitle("Student Management System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Roll Number:"));
        rollNumberField = new JTextField();
        inputPanel.add(rollNumberField);
        inputPanel.add(new JLabel("Grade:"));
        gradeField = new JTextField();
        inputPanel.add(gradeField);

        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });
        inputPanel.add(addButton);

        JButton removeButton = new JButton("Remove Student");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStudent();
            }
        });
        inputPanel.add(removeButton);

        JButton displayButton = new JButton("Display All Students");
        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayStudents();
            }
        });
        inputPanel.add(displayButton);

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    void addStudent() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int rollNumber;
        try {
            rollNumber = Integer.parseInt(rollNumberField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid roll number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String grade = gradeField.getText();
        if (grade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Grade cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = new Student(name, rollNumber, grade);
        sms.addStudent(student);
        outputArea.append("Student added successfully: " + student + "\n");
        clearFields();
    }

    void removeStudent() {
        int rollNumber;
        try {
            rollNumber = Integer.parseInt(rollNumberField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid roll number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student studentToRemove = sms.searchStudent(rollNumber);
        if (studentToRemove != null) {
            sms.removeStudent(rollNumber);
            outputArea.append("Student removed successfully: " + studentToRemove + "\n");
        } else {
            JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        clearFields();
    }

    void displayStudents() {
        outputArea.setText("");
        List<Student> students = sms.getAllStudents();
        if (students.isEmpty()) {
            outputArea.append("No students found.\n");
        } else {
            outputArea.append("All Students:\n");
            for (Student student : students) {
                outputArea.append(student + "\n");
            }
        }
    }

    void clearFields() {
        nameField.setText("");
        rollNumberField.setText("");
        gradeField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StudentManagementGUI().setVisible(true);
            }
        });
    }
}
