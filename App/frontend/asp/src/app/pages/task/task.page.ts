import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TaskService } from "../../services/task.service";
import { AuthService } from "../../services/auth.service";

interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
  ownerUsername: string;
  createdAt: string;
  deadline: string;
  points: number;
  volunteers?: string[];
  showTakeButton?: boolean;
  showCompleteButton?: boolean;
}

@Component({
  selector: 'app-task',
  templateUrl: './task.page.html',
  styleUrls: ['./task.page.scss'],
  standalone: false
})
export class TaskPage implements OnInit {
  tasks: Task[] = [];
  taskForm: FormGroup;
  loading = false;
  error: string | null = null;
  userRole: string | null = null;
  showForm = false;
  username: string = '';
  showCompletedTasks = false;
  isCD: boolean = false;

  constructor(
    private taskService: TaskService,
    private fb: FormBuilder,
    private auth: AuthService
  ) {
    this.taskForm = this.fb.group({
      title: ['', Validators.required],
      description: [''],
      status: ['pending', Validators.required],
      deadline: ['', Validators.required],
      points: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit() {
    this.loadTasks();
    this.getUserRole();
    this.getUsername();
  }

  toggleForm() {
    this.showForm = !this.showForm;
  }

  toggleCompletedTasks() {
    this.showCompletedTasks = !this.showCompletedTasks;
  }

  get completedTasks(): Task[] {
    return this.tasks.filter(t =>
      t.status === 'COMPLETED' &&
      (t.ownerUsername === this.username || t.volunteers?.includes(this.username))
    );
  }

  get activeTasks(): Task[] {
    return this.tasks.filter(t => t.status !== 'COMPLETED');
  }

  async getUserRole(): Promise<void> {
    try {
      this.userRole = await this.auth.getUserRole();
      this.isCD = this.userRole === 'CD' || this.userRole === 'ADMINISTRATOR';
    } catch (err: any) {
      if (err.status === 200) {
        this.userRole = err.error.text;
      }
    }
  }

  async getUsername(): Promise<void> {
    try {
      this.username = await this.auth.getUsername();
    } catch (error: any) {
      console.error('Error getting username:', error);
    }
  }

  async loadTasks() {
    this.loading = true;
    this.error = null;
    try {
      const data: any = await this.taskService.getTasks();
      this.tasks = data;
      for (let task of this.tasks) {
        task.showTakeButton = !task.volunteers?.includes(this.username);
        task.showCompleteButton = task.ownerUsername === this.username && task.status === 'IN_PROGRESS';
      }
    } catch (err: any) {
      if (err.status === 401) {
        this.error = 'Failed to load tasks.';
      }
    } finally {
      this.loading = false;
    }
  }

  async completeTask(task: Task) {
    this.loading = true;
    this.error = null;
    try {
      await this.taskService.completeTask(task.id);
      this.loadTasks();
    } catch (err: any) {
      if (err.status === 401) {
        this.error = 'Failed to complete task.';
      }
    } finally {
      this.loading = false;
    }
  }

  async takeTask(task: Task) {
    this.loading = true;
    this.error = null;
    try {
      const username = await this.auth.getUsername();
      await this.taskService.assignTask(task.id, username);
      this.loadTasks();
    } catch (err: any) {
      if (err.status === 401) {
        this.error = 'Failed to take task.';
      }
    } finally {
      this.loading = false;
    }
  }

  async onSubmit() {
    if (this.taskForm.invalid) return;

    const raw = this.taskForm.value;
    const taskData = {
      ...raw,
      createdAt: new Date().toISOString(),
      ownerUsername: await this.auth.getUsername(),
      status: raw.status.toUpperCase()
    };

    this.loading = true;
    this.error = null;
    try {
      await this.taskService.createTask(taskData);
      this.taskForm.reset({ status: 'pending', points: 0 });
      this.showForm = false;
      this.loadTasks();
    } catch (err: any) {
      if (err.status === 401) {
        this.error = 'Failed to create task.';
      }
    } finally {
      this.loading = false;
    }
  }
}
